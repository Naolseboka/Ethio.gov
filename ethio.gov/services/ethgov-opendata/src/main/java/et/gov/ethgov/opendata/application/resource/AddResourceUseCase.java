package et.gov.ethgov.opendata.application.resource;

import et.gov.ethgov.opendata.application.OpenDataEventPublisher;
import et.gov.ethgov.opendata.domain.dataset.DatasetId;
import et.gov.ethgov.opendata.domain.dataset.DatasetRepository;
import et.gov.ethgov.opendata.domain.dataset.DatasetStatus;
import et.gov.ethgov.opendata.domain.dataset.OpenDataset;
import et.gov.ethgov.opendata.domain.resource.DatasetResource;
import et.gov.ethgov.opendata.domain.resource.ResourceFormat;
import et.gov.ethgov.opendata.domain.resource.ResourceId;
import et.gov.ethgov.opendata.domain.resource.ResourceNumber;
import et.gov.ethgov.opendata.domain.resource.ResourceNumberSequence;
import et.gov.ethgov.opendata.domain.resource.ResourceRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddResourceUseCase {

    private final DatasetRepository datasets;
    private final ResourceRepository resources;
    private final ResourceNumberSequence sequences;
    private final OpenDataEventPublisher eventPublisher;
    private final Clock clock;

    public AddResourceUseCase(
            DatasetRepository datasets,
            ResourceRepository resources,
            ResourceNumberSequence sequences,
            OpenDataEventPublisher eventPublisher,
            Clock clock) {
        this.datasets = datasets;
        this.resources = resources;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public ResourceId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        OpenDataset dataset = datasets
                .findById(DatasetId.of(command.datasetId()))
                .orElseThrow(() -> new DomainException(
                        "DATASET_NOT_FOUND", "Dataset Not Found", "Dataset does not exist", 404));
        if (!dataset.tenantId().equals(tenantId)) {
            throw new DomainException(
                    "DATASET_TENANT_MISMATCH",
                    "Dataset Tenant Mismatch",
                    "Dataset does not belong to the requested tenant",
                    409);
        }
        if (dataset.status() != DatasetStatus.DRAFT && dataset.status() != DatasetStatus.PUBLISHED) {
            throw new DomainException(
                    "DATASET_NOT_ACCEPTING_RESOURCES",
                    "Dataset Not Accepting Resources",
                    "Resources can only be added to DRAFT or PUBLISHED datasets",
                    409);
        }
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        ResourceNumber resourceNumber =
                ResourceNumber.allocate(year, sequences.nextSequence(tenantId, year));
        DatasetResource resource = DatasetResource.add(
                tenantId,
                resourceNumber,
                dataset.id(),
                command.title(),
                command.description(),
                ResourceFormat.valueOf(command.format()),
                command.accessUrl(),
                command.documentId(),
                command.mediaType(),
                clock.instant());
        resources.save(resource);
        eventPublisher.publishResourceEvents(resource.pullDomainEvents());
        return resource.id();
    }

    public record Command(
            String tenantId,
            String datasetId,
            String title,
            String description,
            String format,
            String accessUrl,
            String documentId,
            String mediaType) {}
}

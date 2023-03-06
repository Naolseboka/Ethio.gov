package et.gov.ethgov.opendata.application.dataset;

import et.gov.ethgov.opendata.application.OpenDataEventPublisher;
import et.gov.ethgov.opendata.domain.dataset.DatasetId;
import et.gov.ethgov.opendata.domain.dataset.DatasetNumber;
import et.gov.ethgov.opendata.domain.dataset.DatasetNumberSequence;
import et.gov.ethgov.opendata.domain.dataset.DatasetRepository;
import et.gov.ethgov.opendata.domain.dataset.DatasetSlug;
import et.gov.ethgov.opendata.domain.dataset.DatasetTheme;
import et.gov.ethgov.opendata.domain.dataset.OpenDataset;
import et.gov.ethgov.opendata.domain.dataset.OpenLicense;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterDatasetUseCase {

    private final DatasetRepository datasets;
    private final DatasetNumberSequence sequences;
    private final OpenDataEventPublisher eventPublisher;
    private final Clock clock;

    public RegisterDatasetUseCase(
            DatasetRepository datasets,
            DatasetNumberSequence sequences,
            OpenDataEventPublisher eventPublisher,
            Clock clock) {
        this.datasets = datasets;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public DatasetId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        DatasetSlug slug = DatasetSlug.of(command.slug());
        if (datasets.existsByTenantAndSlug(tenantId, slug)) {
            throw new DomainException(
                    "DATASET_SLUG_EXISTS",
                    "Dataset Slug Exists",
                    "A dataset with this slug already exists for the tenant",
                    409);
        }
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        DatasetNumber datasetNumber = DatasetNumber.allocate(year, sequences.nextSequence(tenantId, year));
        OpenDataset dataset = OpenDataset.register(
                tenantId,
                datasetNumber,
                slug,
                command.title(),
                command.description(),
                DatasetTheme.valueOf(command.theme()),
                OpenLicense.valueOf(command.license()),
                command.publisherOrgId(),
                command.contactEmail(),
                command.locale(),
                clock.instant());
        datasets.save(dataset);
        eventPublisher.publishDatasetEvents(dataset.pullDomainEvents());
        return dataset.id();
    }

    public record Command(
            String tenantId,
            String slug,
            String title,
            String description,
            String theme,
            String license,
            String publisherOrgId,
            String contactEmail,
            String locale) {}
}

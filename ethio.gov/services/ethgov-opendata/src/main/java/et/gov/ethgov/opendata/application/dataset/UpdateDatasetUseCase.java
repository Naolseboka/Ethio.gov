package et.gov.ethgov.opendata.application.dataset;

import et.gov.ethgov.opendata.application.OpenDataEventPublisher;
import et.gov.ethgov.opendata.domain.dataset.DatasetId;
import et.gov.ethgov.opendata.domain.dataset.DatasetRepository;
import et.gov.ethgov.opendata.domain.dataset.DatasetTheme;
import et.gov.ethgov.opendata.domain.dataset.OpenDataset;
import et.gov.ethgov.opendata.domain.dataset.OpenLicense;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateDatasetUseCase {

    private final DatasetRepository datasets;
    private final OpenDataEventPublisher eventPublisher;
    private final Clock clock;

    public UpdateDatasetUseCase(
            DatasetRepository datasets, OpenDataEventPublisher eventPublisher, Clock clock) {
        this.datasets = datasets;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(Command command) {
        Objects.requireNonNull(command, "command");
        OpenDataset dataset = datasets
                .findById(DatasetId.of(command.datasetId()))
                .orElseThrow(() -> new DomainException(
                        "DATASET_NOT_FOUND", "Dataset Not Found", "Dataset does not exist", 404));
        dataset.update(
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
    }

    public record Command(
            String datasetId,
            String title,
            String description,
            String theme,
            String license,
            String publisherOrgId,
            String contactEmail,
            String locale) {}
}

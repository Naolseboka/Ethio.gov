package et.gov.ethgov.opendata.application.dataset;

import et.gov.ethgov.opendata.application.OpenDataEventPublisher;
import et.gov.ethgov.opendata.domain.dataset.DatasetId;
import et.gov.ethgov.opendata.domain.dataset.DatasetRepository;
import et.gov.ethgov.opendata.domain.dataset.OpenDataset;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetireDatasetUseCase {

    private final DatasetRepository datasets;
    private final OpenDataEventPublisher eventPublisher;
    private final Clock clock;

    public RetireDatasetUseCase(
            DatasetRepository datasets, OpenDataEventPublisher eventPublisher, Clock clock) {
        this.datasets = datasets;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String datasetId) {
        Objects.requireNonNull(datasetId, "datasetId");
        OpenDataset dataset = datasets
                .findById(DatasetId.of(datasetId))
                .orElseThrow(() -> new DomainException(
                        "DATASET_NOT_FOUND", "Dataset Not Found", "Dataset does not exist", 404));
        dataset.retire(clock.instant());
        datasets.save(dataset);
        eventPublisher.publishDatasetEvents(dataset.pullDomainEvents());
    }
}

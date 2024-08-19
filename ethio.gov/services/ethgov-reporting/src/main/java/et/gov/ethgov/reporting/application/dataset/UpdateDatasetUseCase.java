package et.gov.ethgov.reporting.application.dataset;

import et.gov.ethgov.reporting.domain.dataset.Dataset;
import et.gov.ethgov.reporting.domain.dataset.DatasetId;
import et.gov.ethgov.reporting.domain.dataset.DatasetRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateDatasetUseCase {
    private final DatasetRepository repository;
    private final Clock clock;

    public UpdateDatasetUseCase(DatasetRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DatasetId execute(UpdateDatasetCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DatasetId id = DatasetId.parse(command.datasetId());
        Dataset aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("dataset not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateDatasetCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String datasetId,
            String reason) {}
}

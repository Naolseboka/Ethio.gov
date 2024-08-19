package et.gov.ethgov.reporting.application.dataset;

import et.gov.ethgov.reporting.domain.dataset.Dataset;
import et.gov.ethgov.reporting.domain.dataset.DatasetId;
import et.gov.ethgov.reporting.domain.dataset.DatasetRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDatasetUseCase {
    private final DatasetRepository repository;
    private final Clock clock;

    public CreateDatasetUseCase(DatasetRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DatasetId execute(CreateDatasetCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Dataset aggregate = Dataset.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDatasetCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String datasetId,
            String reason) {}
}

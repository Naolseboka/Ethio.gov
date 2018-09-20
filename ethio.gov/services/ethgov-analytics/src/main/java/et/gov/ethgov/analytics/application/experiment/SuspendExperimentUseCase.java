package et.gov.ethgov.analytics.application.experiment;

import et.gov.ethgov.analytics.domain.experiment.Experiment;
import et.gov.ethgov.analytics.domain.experiment.ExperimentId;
import et.gov.ethgov.analytics.domain.experiment.ExperimentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendExperimentUseCase {
    private final ExperimentRepository repository;
    private final Clock clock;

    public SuspendExperimentUseCase(ExperimentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ExperimentId execute(SuspendExperimentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ExperimentId id = ExperimentId.parse(command.experimentId());
        Experiment aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("experiment not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendExperimentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String experimentId,
            String reason) {}
}

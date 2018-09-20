package et.gov.ethgov.analytics.application.experiment;

import et.gov.ethgov.analytics.domain.experiment.Experiment;
import et.gov.ethgov.analytics.domain.experiment.ExperimentId;
import et.gov.ethgov.analytics.domain.experiment.ExperimentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateExperimentUseCase {
    private final ExperimentRepository repository;
    private final Clock clock;

    public UpdateExperimentUseCase(ExperimentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ExperimentId execute(UpdateExperimentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ExperimentId id = ExperimentId.parse(command.experimentId());
        Experiment aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("experiment not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateExperimentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String experimentId,
            String reason) {}
}

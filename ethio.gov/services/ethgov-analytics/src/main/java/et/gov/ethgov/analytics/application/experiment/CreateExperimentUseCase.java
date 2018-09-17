package et.gov.ethgov.analytics.application.experiment;

import et.gov.ethgov.analytics.domain.experiment.Experiment;
import et.gov.ethgov.analytics.domain.experiment.ExperimentId;
import et.gov.ethgov.analytics.domain.experiment.ExperimentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateExperimentUseCase {
    private final ExperimentRepository repository;
    private final Clock clock;

    public CreateExperimentUseCase(ExperimentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ExperimentId execute(CreateExperimentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Experiment aggregate = Experiment.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateExperimentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String experimentId,
            String reason) {}
}

package et.gov.ethgov.workflow.application.transition;

import et.gov.ethgov.workflow.domain.transition.Transition;
import et.gov.ethgov.workflow.domain.transition.TransitionId;
import et.gov.ethgov.workflow.domain.transition.TransitionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateTransitionUseCase {
    private final TransitionRepository repository;
    private final Clock clock;

    public CreateTransitionUseCase(TransitionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TransitionId execute(CreateTransitionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Transition aggregate = Transition.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateTransitionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String transitionId,
            String reason) {}
}

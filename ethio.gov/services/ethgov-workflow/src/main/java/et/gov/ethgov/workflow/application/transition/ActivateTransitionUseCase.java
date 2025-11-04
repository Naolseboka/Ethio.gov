package et.gov.ethgov.workflow.application.transition;

import et.gov.ethgov.workflow.domain.transition.Transition;
import et.gov.ethgov.workflow.domain.transition.TransitionId;
import et.gov.ethgov.workflow.domain.transition.TransitionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateTransitionUseCase {
    private final TransitionRepository repository;
    private final Clock clock;

    public ActivateTransitionUseCase(TransitionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TransitionId execute(ActivateTransitionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TransitionId id = TransitionId.parse(command.transitionId());
        Transition aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("transition not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateTransitionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String transitionId,
            String reason) {}
}

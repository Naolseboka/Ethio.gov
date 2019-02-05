package et.gov.ethgov.case.application.decision;

import et.gov.ethgov.case.domain.decision.Decision;
import et.gov.ethgov.case.domain.decision.DecisionId;
import et.gov.ethgov.case.domain.decision.DecisionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDecisionUseCase {
    private final DecisionRepository repository;
    private final Clock clock;

    public CreateDecisionUseCase(DecisionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DecisionId execute(CreateDecisionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Decision aggregate = Decision.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDecisionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String decisionId,
            String reason) {}
}

package et.gov.ethgov.case.application.decision;

import et.gov.ethgov.case.domain.decision.Decision;
import et.gov.ethgov.case.domain.decision.DecisionId;
import et.gov.ethgov.case.domain.decision.DecisionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateDecisionUseCase {
    private final DecisionRepository repository;
    private final Clock clock;

    public UpdateDecisionUseCase(DecisionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DecisionId execute(UpdateDecisionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DecisionId id = DecisionId.parse(command.decisionId());
        Decision aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("decision not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateDecisionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String decisionId,
            String reason) {}
}

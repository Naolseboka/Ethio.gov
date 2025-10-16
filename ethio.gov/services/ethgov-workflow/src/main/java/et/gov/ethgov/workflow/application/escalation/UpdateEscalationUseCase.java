package et.gov.ethgov.workflow.application.escalation;

import et.gov.ethgov.workflow.domain.escalation.Escalation;
import et.gov.ethgov.workflow.domain.escalation.EscalationId;
import et.gov.ethgov.workflow.domain.escalation.EscalationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateEscalationUseCase {
    private final EscalationRepository repository;
    private final Clock clock;

    public UpdateEscalationUseCase(EscalationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EscalationId execute(UpdateEscalationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        EscalationId id = EscalationId.parse(command.escalationId());
        Escalation aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("escalation not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateEscalationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String escalationId,
            String reason) {}
}

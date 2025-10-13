package et.gov.ethgov.workflow.application.escalation;

import et.gov.ethgov.workflow.domain.escalation.Escalation;
import et.gov.ethgov.workflow.domain.escalation.EscalationId;
import et.gov.ethgov.workflow.domain.escalation.EscalationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateEscalationUseCase {
    private final EscalationRepository repository;
    private final Clock clock;

    public CreateEscalationUseCase(EscalationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EscalationId execute(CreateEscalationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Escalation aggregate = Escalation.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateEscalationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String escalationId,
            String reason) {}
}

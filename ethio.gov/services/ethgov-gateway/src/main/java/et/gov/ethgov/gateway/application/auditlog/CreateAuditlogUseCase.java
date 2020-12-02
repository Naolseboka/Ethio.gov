package et.gov.ethgov.gateway.application.auditlog;

import et.gov.ethgov.gateway.domain.auditlog.Auditlog;
import et.gov.ethgov.gateway.domain.auditlog.AuditlogId;
import et.gov.ethgov.gateway.domain.auditlog.AuditlogRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateAuditlogUseCase {
    private final AuditlogRepository repository;
    private final Clock clock;

    public CreateAuditlogUseCase(AuditlogRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AuditlogId execute(CreateAuditlogCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Auditlog aggregate = Auditlog.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateAuditlogCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String auditlogId,
            String reason) {}
}

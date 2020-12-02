package et.gov.ethgov.gateway.application.auditlog;

import et.gov.ethgov.gateway.domain.auditlog.Auditlog;
import et.gov.ethgov.gateway.domain.auditlog.AuditlogId;
import et.gov.ethgov.gateway.domain.auditlog.AuditlogRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateAuditlogUseCase {
    private final AuditlogRepository repository;
    private final Clock clock;

    public ActivateAuditlogUseCase(AuditlogRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AuditlogId execute(ActivateAuditlogCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AuditlogId id = AuditlogId.parse(command.auditlogId());
        Auditlog aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("auditlog not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateAuditlogCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String auditlogId,
            String reason) {}
}

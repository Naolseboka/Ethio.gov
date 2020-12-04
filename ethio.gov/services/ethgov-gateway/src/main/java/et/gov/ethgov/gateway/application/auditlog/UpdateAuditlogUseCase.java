package et.gov.ethgov.gateway.application.auditlog;

import et.gov.ethgov.gateway.domain.auditlog.Auditlog;
import et.gov.ethgov.gateway.domain.auditlog.AuditlogId;
import et.gov.ethgov.gateway.domain.auditlog.AuditlogRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateAuditlogUseCase {
    private final AuditlogRepository repository;
    private final Clock clock;

    public UpdateAuditlogUseCase(AuditlogRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AuditlogId execute(UpdateAuditlogCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AuditlogId id = AuditlogId.parse(command.auditlogId());
        Auditlog aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("auditlog not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateAuditlogCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String auditlogId,
            String reason) {}
}

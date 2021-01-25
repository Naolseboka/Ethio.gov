package et.gov.ethgov.gateway.domain.auditlog;

import java.time.Instant;
import java.util.Objects;

/** Domain event: AuditlogClosed. */
public final class AuditlogClosed {
    private final AuditlogId id;
    private final Instant occurredAt;

    public AuditlogClosed(AuditlogId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public AuditlogId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

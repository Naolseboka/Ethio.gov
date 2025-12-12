package et.gov.ethgov.workflow.domain.escalation;

import java.time.Instant;
import java.util.Objects;

/** Domain event: EscalationSuspended. */
public final class EscalationSuspended {
    private final EscalationId id;
    private final Instant occurredAt;

    public EscalationSuspended(EscalationId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public EscalationId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

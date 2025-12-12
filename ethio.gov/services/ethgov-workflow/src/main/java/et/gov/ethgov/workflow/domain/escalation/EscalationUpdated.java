package et.gov.ethgov.workflow.domain.escalation;

import java.time.Instant;
import java.util.Objects;

/** Domain event: EscalationUpdated. */
public final class EscalationUpdated {
    private final EscalationId id;
    private final Instant occurredAt;

    public EscalationUpdated(EscalationId id, Instant occurredAt) {
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

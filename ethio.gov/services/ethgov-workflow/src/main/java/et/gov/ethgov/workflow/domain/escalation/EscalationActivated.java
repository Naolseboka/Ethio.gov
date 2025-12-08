package et.gov.ethgov.workflow.domain.escalation;

import java.time.Instant;
import java.util.Objects;

/** Domain event: EscalationActivated. */
public final class EscalationActivated {
    private final EscalationId id;
    private final Instant occurredAt;

    public EscalationActivated(EscalationId id, Instant occurredAt) {
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

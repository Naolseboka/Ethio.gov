package et.gov.ethgov.workflow.domain.escalation;

import java.time.Instant;
import java.util.Objects;

/** Domain event: EscalationCreated. */
public final class EscalationCreated {
    private final EscalationId id;
    private final Instant occurredAt;

    public EscalationCreated(EscalationId id, Instant occurredAt) {
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

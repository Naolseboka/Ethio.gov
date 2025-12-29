package et.gov.ethgov.workflow.domain.sla;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SlaActivated. */
public final class SlaActivated {
    private final SlaId id;
    private final Instant occurredAt;

    public SlaActivated(SlaId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SlaId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

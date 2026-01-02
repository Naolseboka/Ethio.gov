package et.gov.ethgov.workflow.domain.sla;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SlaSuspended. */
public final class SlaSuspended {
    private final SlaId id;
    private final Instant occurredAt;

    public SlaSuspended(SlaId id, Instant occurredAt) {
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

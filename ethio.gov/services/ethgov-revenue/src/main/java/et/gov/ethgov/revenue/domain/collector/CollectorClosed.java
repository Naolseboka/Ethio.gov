package et.gov.ethgov.revenue.domain.collector;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CollectorClosed. */
public final class CollectorClosed {
    private final CollectorId id;
    private final Instant occurredAt;

    public CollectorClosed(CollectorId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CollectorId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

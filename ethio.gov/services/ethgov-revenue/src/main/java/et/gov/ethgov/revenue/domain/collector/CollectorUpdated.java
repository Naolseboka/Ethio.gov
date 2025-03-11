package et.gov.ethgov.revenue.domain.collector;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CollectorUpdated. */
public final class CollectorUpdated {
    private final CollectorId id;
    private final Instant occurredAt;

    public CollectorUpdated(CollectorId id, Instant occurredAt) {
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

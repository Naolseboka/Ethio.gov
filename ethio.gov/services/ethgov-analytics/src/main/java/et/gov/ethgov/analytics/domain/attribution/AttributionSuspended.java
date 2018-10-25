package et.gov.ethgov.analytics.domain.attribution;

import java.time.Instant;
import java.util.Objects;

/** Domain event: AttributionSuspended. */
public final class AttributionSuspended {
    private final AttributionId id;
    private final Instant occurredAt;

    public AttributionSuspended(AttributionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public AttributionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

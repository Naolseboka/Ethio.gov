package et.gov.ethgov.gis.domain.spatialquery;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SpatialquerySuspended. */
public final class SpatialquerySuspended {
    private final SpatialqueryId id;
    private final Instant occurredAt;

    public SpatialquerySuspended(SpatialqueryId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SpatialqueryId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

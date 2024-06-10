package et.gov.ethgov.permits.domain.occupancy;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OccupancyUpdated. */
public final class OccupancyUpdated {
    private final OccupancyId id;
    private final Instant occurredAt;

    public OccupancyUpdated(OccupancyId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public OccupancyId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

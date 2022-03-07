package et.gov.ethgov.land.domain.boundary;

import java.time.Instant;
import java.util.Objects;

/** Domain event: BoundarySuspended. */
public final class BoundarySuspended {
    private final BoundaryId id;
    private final Instant occurredAt;

    public BoundarySuspended(BoundaryId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public BoundaryId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

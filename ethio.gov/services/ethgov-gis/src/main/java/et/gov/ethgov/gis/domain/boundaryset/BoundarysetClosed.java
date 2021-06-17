package et.gov.ethgov.gis.domain.boundaryset;

import java.time.Instant;
import java.util.Objects;

/** Domain event: BoundarysetClosed. */
public final class BoundarysetClosed {
    private final BoundarysetId id;
    private final Instant occurredAt;

    public BoundarysetClosed(BoundarysetId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public BoundarysetId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

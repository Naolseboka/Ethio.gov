package et.gov.ethgov.gis.domain.boundaryset;

import java.time.Instant;
import java.util.Objects;

/** Domain event: BoundarysetCreated. */
public final class BoundarysetCreated {
    private final BoundarysetId id;
    private final Instant occurredAt;

    public BoundarysetCreated(BoundarysetId id, Instant occurredAt) {
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

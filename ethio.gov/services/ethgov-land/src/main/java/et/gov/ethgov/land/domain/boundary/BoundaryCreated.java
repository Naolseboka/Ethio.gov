package et.gov.ethgov.land.domain.boundary;

import java.time.Instant;
import java.util.Objects;

/** Domain event: BoundaryCreated. */
public final class BoundaryCreated {
    private final BoundaryId id;
    private final Instant occurredAt;

    public BoundaryCreated(BoundaryId id, Instant occurredAt) {
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

package et.gov.ethgov.analytics.domain.cube;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CubeActivated. */
public final class CubeActivated {
    private final CubeId id;
    private final Instant occurredAt;

    public CubeActivated(CubeId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CubeId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

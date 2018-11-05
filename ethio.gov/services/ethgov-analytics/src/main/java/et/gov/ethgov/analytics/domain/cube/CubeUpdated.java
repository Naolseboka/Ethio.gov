package et.gov.ethgov.analytics.domain.cube;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CubeUpdated. */
public final class CubeUpdated {
    private final CubeId id;
    private final Instant occurredAt;

    public CubeUpdated(CubeId id, Instant occurredAt) {
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

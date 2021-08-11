package et.gov.ethgov.gis.domain.tilecache;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TilecacheUpdated. */
public final class TilecacheUpdated {
    private final TilecacheId id;
    private final Instant occurredAt;

    public TilecacheUpdated(TilecacheId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public TilecacheId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

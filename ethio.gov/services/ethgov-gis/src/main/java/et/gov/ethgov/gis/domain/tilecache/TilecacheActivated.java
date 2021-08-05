package et.gov.ethgov.gis.domain.tilecache;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TilecacheActivated. */
public final class TilecacheActivated {
    private final TilecacheId id;
    private final Instant occurredAt;

    public TilecacheActivated(TilecacheId id, Instant occurredAt) {
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

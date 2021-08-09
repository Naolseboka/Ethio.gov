package et.gov.ethgov.gis.domain.tilecache;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TilecacheClosed. */
public final class TilecacheClosed {
    private final TilecacheId id;
    private final Instant occurredAt;

    public TilecacheClosed(TilecacheId id, Instant occurredAt) {
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

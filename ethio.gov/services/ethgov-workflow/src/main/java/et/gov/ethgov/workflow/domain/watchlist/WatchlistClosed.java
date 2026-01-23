package et.gov.ethgov.workflow.domain.watchlist;

import java.time.Instant;
import java.util.Objects;

/** Domain event: WatchlistClosed. */
public final class WatchlistClosed {
    private final WatchlistId id;
    private final Instant occurredAt;

    public WatchlistClosed(WatchlistId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public WatchlistId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

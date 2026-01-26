package et.gov.ethgov.workflow.domain.watchlist;

import java.time.Instant;
import java.util.Objects;

/** Domain event: WatchlistSuspended. */
public final class WatchlistSuspended {
    private final WatchlistId id;
    private final Instant occurredAt;

    public WatchlistSuspended(WatchlistId id, Instant occurredAt) {
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

package et.gov.ethgov.workflow.domain.watchlist;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Watchlist. */
public final class WatchlistId {
    private final UUID value;

    private WatchlistId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static WatchlistId newId() {
        return new WatchlistId(UUID.randomUUID());
    }

    public static WatchlistId of(UUID value) {
        return new WatchlistId(value);
    }

    public static WatchlistId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new WatchlistId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WatchlistId other)) {
            return false;
        }
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

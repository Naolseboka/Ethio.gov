package et.gov.ethgov.search.domain.ranking;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Ranking. */
public final class RankingId {
    private final UUID value;

    private RankingId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static RankingId newId() {
        return new RankingId(UUID.randomUUID());
    }

    public static RankingId of(UUID value) {
        return new RankingId(value);
    }

    public static RankingId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new RankingId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RankingId other)) {
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

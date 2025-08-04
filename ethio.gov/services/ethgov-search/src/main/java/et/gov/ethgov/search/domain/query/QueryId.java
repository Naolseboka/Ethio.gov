package et.gov.ethgov.search.domain.query;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Query. */
public final class QueryId {
    private final UUID value;

    private QueryId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static QueryId newId() {
        return new QueryId(UUID.randomUUID());
    }

    public static QueryId of(UUID value) {
        return new QueryId(value);
    }

    public static QueryId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new QueryId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QueryId other)) {
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

package et.gov.ethgov.search.domain.stopword;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Stopword. */
public final class StopwordId {
    private final UUID value;

    private StopwordId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static StopwordId newId() {
        return new StopwordId(UUID.randomUUID());
    }

    public static StopwordId of(UUID value) {
        return new StopwordId(value);
    }

    public static StopwordId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new StopwordId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StopwordId other)) {
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

package et.gov.ethgov.revenue.domain.collector;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Collector. */
public final class CollectorId {
    private final UUID value;

    private CollectorId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CollectorId newId() {
        return new CollectorId(UUID.randomUUID());
    }

    public static CollectorId of(UUID value) {
        return new CollectorId(value);
    }

    public static CollectorId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CollectorId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CollectorId other)) {
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

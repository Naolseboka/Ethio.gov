package et.gov.ethgov.reporting.domain.metric;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Metric. */
public final class MetricId {
    private final UUID value;

    private MetricId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static MetricId newId() {
        return new MetricId(UUID.randomUUID());
    }

    public static MetricId of(UUID value) {
        return new MetricId(value);
    }

    public static MetricId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new MetricId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MetricId other)) {
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

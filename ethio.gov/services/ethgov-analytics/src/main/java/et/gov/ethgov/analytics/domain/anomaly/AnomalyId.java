package et.gov.ethgov.analytics.domain.anomaly;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Anomaly. */
public final class AnomalyId {
    private final UUID value;

    private AnomalyId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static AnomalyId newId() {
        return new AnomalyId(UUID.randomUUID());
    }

    public static AnomalyId of(UUID value) {
        return new AnomalyId(value);
    }

    public static AnomalyId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new AnomalyId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AnomalyId other)) {
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

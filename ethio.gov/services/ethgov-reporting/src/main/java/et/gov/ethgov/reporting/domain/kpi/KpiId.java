package et.gov.ethgov.reporting.domain.kpi;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Kpi. */
public final class KpiId {
    private final UUID value;

    private KpiId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static KpiId newId() {
        return new KpiId(UUID.randomUUID());
    }

    public static KpiId of(UUID value) {
        return new KpiId(value);
    }

    public static KpiId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new KpiId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KpiId other)) {
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

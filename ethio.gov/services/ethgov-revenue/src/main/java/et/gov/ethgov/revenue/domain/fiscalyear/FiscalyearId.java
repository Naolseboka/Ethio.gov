package et.gov.ethgov.revenue.domain.fiscalyear;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Fiscalyear. */
public final class FiscalyearId {
    private final UUID value;

    private FiscalyearId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static FiscalyearId newId() {
        return new FiscalyearId(UUID.randomUUID());
    }

    public static FiscalyearId of(UUID value) {
        return new FiscalyearId(value);
    }

    public static FiscalyearId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new FiscalyearId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FiscalyearId other)) {
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

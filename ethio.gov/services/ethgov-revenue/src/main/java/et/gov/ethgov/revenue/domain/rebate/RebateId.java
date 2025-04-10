package et.gov.ethgov.revenue.domain.rebate;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Rebate. */
public final class RebateId {
    private final UUID value;

    private RebateId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static RebateId newId() {
        return new RebateId(UUID.randomUUID());
    }

    public static RebateId of(UUID value) {
        return new RebateId(value);
    }

    public static RebateId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new RebateId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RebateId other)) {
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

package et.gov.ethgov.revenue.domain.revenueappeal;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Revenueappeal. */
public final class RevenueappealId {
    private final UUID value;

    private RevenueappealId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static RevenueappealId newId() {
        return new RevenueappealId(UUID.randomUUID());
    }

    public static RevenueappealId of(UUID value) {
        return new RevenueappealId(value);
    }

    public static RevenueappealId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new RevenueappealId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RevenueappealId other)) {
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

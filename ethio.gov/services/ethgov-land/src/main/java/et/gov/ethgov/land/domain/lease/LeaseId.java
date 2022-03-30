package et.gov.ethgov.land.domain.lease;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Lease. */
public final class LeaseId {
    private final UUID value;

    private LeaseId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static LeaseId newId() {
        return new LeaseId(UUID.randomUUID());
    }

    public static LeaseId of(UUID value) {
        return new LeaseId(value);
    }

    public static LeaseId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new LeaseId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LeaseId other)) {
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

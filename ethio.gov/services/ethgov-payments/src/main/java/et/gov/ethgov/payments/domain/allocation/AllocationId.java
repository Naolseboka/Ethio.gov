package et.gov.ethgov.payments.domain.allocation;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Allocation. */
public final class AllocationId {
    private final UUID value;

    private AllocationId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static AllocationId newId() {
        return new AllocationId(UUID.randomUUID());
    }

    public static AllocationId of(UUID value) {
        return new AllocationId(value);
    }

    public static AllocationId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new AllocationId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AllocationId other)) {
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

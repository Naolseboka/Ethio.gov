package et.gov.ethgov.identity.domain.delegation;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Delegation. */
public final class DelegationId {
    private final UUID value;

    private DelegationId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DelegationId newId() {
        return new DelegationId(UUID.randomUUID());
    }

    public static DelegationId of(UUID value) {
        return new DelegationId(value);
    }

    public static DelegationId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DelegationId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DelegationId other)) {
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

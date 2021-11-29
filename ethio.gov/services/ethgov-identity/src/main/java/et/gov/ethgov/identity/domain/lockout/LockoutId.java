package et.gov.ethgov.identity.domain.lockout;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Lockout. */
public final class LockoutId {
    private final UUID value;

    private LockoutId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static LockoutId newId() {
        return new LockoutId(UUID.randomUUID());
    }

    public static LockoutId of(UUID value) {
        return new LockoutId(value);
    }

    public static LockoutId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new LockoutId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LockoutId other)) {
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

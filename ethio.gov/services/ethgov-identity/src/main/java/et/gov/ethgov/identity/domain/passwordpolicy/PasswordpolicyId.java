package et.gov.ethgov.identity.domain.passwordpolicy;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Passwordpolicy. */
public final class PasswordpolicyId {
    private final UUID value;

    private PasswordpolicyId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PasswordpolicyId newId() {
        return new PasswordpolicyId(UUID.randomUUID());
    }

    public static PasswordpolicyId of(UUID value) {
        return new PasswordpolicyId(value);
    }

    public static PasswordpolicyId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PasswordpolicyId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PasswordpolicyId other)) {
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

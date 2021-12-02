package et.gov.ethgov.identity.domain.mfa;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Mfa. */
public final class MfaId {
    private final UUID value;

    private MfaId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static MfaId newId() {
        return new MfaId(UUID.randomUUID());
    }

    public static MfaId of(UUID value) {
        return new MfaId(value);
    }

    public static MfaId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new MfaId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MfaId other)) {
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

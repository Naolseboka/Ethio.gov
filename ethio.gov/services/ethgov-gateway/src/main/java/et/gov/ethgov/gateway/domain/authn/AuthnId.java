package et.gov.ethgov.gateway.domain.authn;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Authn. */
public final class AuthnId {
    private final UUID value;

    private AuthnId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static AuthnId newId() {
        return new AuthnId(UUID.randomUUID());
    }

    public static AuthnId of(UUID value) {
        return new AuthnId(value);
    }

    public static AuthnId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new AuthnId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuthnId other)) {
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

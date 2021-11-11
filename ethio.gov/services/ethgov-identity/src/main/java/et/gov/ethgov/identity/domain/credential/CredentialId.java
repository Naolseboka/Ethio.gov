package et.gov.ethgov.identity.domain.credential;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Credential. */
public final class CredentialId {
    private final UUID value;

    private CredentialId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CredentialId newId() {
        return new CredentialId(UUID.randomUUID());
    }

    public static CredentialId of(UUID value) {
        return new CredentialId(value);
    }

    public static CredentialId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CredentialId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CredentialId other)) {
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

package et.gov.ethgov.licensing.domain.revocation;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Revocation. */
public final class RevocationId {
    private final UUID value;

    private RevocationId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static RevocationId newId() {
        return new RevocationId(UUID.randomUUID());
    }

    public static RevocationId of(UUID value) {
        return new RevocationId(value);
    }

    public static RevocationId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new RevocationId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RevocationId other)) {
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

package et.gov.ethgov.notification.domain.digest;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Digest. */
public final class DigestId {
    private final UUID value;

    private DigestId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DigestId newId() {
        return new DigestId(UUID.randomUUID());
    }

    public static DigestId of(UUID value) {
        return new DigestId(value);
    }

    public static DigestId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DigestId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DigestId other)) {
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

package et.gov.ethgov.directory.domain.publiclisting;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Publiclisting. */
public final class PubliclistingId {
    private final UUID value;

    private PubliclistingId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PubliclistingId newId() {
        return new PubliclistingId(UUID.randomUUID());
    }

    public static PubliclistingId of(UUID value) {
        return new PubliclistingId(value);
    }

    public static PubliclistingId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PubliclistingId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PubliclistingId other)) {
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

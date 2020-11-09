package et.gov.ethgov.document.domain.versioning;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Versioning. */
public final class VersioningId {
    private final UUID value;

    private VersioningId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static VersioningId newId() {
        return new VersioningId(UUID.randomUUID());
    }

    public static VersioningId of(UUID value) {
        return new VersioningId(value);
    }

    public static VersioningId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new VersioningId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VersioningId other)) {
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

package et.gov.ethgov.gis.domain.tilecache;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Tilecache. */
public final class TilecacheId {
    private final UUID value;

    private TilecacheId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static TilecacheId newId() {
        return new TilecacheId(UUID.randomUUID());
    }

    public static TilecacheId of(UUID value) {
        return new TilecacheId(value);
    }

    public static TilecacheId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new TilecacheId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TilecacheId other)) {
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

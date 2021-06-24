package et.gov.ethgov.gis.domain.crs;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Crs. */
public final class CrsId {
    private final UUID value;

    private CrsId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CrsId newId() {
        return new CrsId(UUID.randomUUID());
    }

    public static CrsId of(UUID value) {
        return new CrsId(value);
    }

    public static CrsId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CrsId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CrsId other)) {
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

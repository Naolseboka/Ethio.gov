package et.gov.ethgov.gis.domain.basemap;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Basemap. */
public final class BasemapId {
    private final UUID value;

    private BasemapId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static BasemapId newId() {
        return new BasemapId(UUID.randomUUID());
    }

    public static BasemapId of(UUID value) {
        return new BasemapId(value);
    }

    public static BasemapId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new BasemapId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BasemapId other)) {
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

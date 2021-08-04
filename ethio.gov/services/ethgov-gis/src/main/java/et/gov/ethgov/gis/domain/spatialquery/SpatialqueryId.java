package et.gov.ethgov.gis.domain.spatialquery;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Spatialquery. */
public final class SpatialqueryId {
    private final UUID value;

    private SpatialqueryId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SpatialqueryId newId() {
        return new SpatialqueryId(UUID.randomUUID());
    }

    public static SpatialqueryId of(UUID value) {
        return new SpatialqueryId(value);
    }

    public static SpatialqueryId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SpatialqueryId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpatialqueryId other)) {
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

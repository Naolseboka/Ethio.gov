package et.gov.ethgov.gis.domain.geocode;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Geocode. */
public final class GeocodeId {
    private final UUID value;

    private GeocodeId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static GeocodeId newId() {
        return new GeocodeId(UUID.randomUUID());
    }

    public static GeocodeId of(UUID value) {
        return new GeocodeId(value);
    }

    public static GeocodeId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new GeocodeId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GeocodeId other)) {
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

package et.gov.ethgov.gis.domain.gisexport;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Gisexport. */
public final class GisexportId {
    private final UUID value;

    private GisexportId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static GisexportId newId() {
        return new GisexportId(UUID.randomUUID());
    }

    public static GisexportId of(UUID value) {
        return new GisexportId(value);
    }

    public static GisexportId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new GisexportId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GisexportId other)) {
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

package et.gov.ethgov.gis.domain.heatmap;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Heatmap. */
public final class HeatmapId {
    private final UUID value;

    private HeatmapId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static HeatmapId newId() {
        return new HeatmapId(UUID.randomUUID());
    }

    public static HeatmapId of(UUID value) {
        return new HeatmapId(value);
    }

    public static HeatmapId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new HeatmapId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HeatmapId other)) {
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

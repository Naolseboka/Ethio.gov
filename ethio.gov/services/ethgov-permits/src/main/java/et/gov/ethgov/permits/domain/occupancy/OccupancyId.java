package et.gov.ethgov.permits.domain.occupancy;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Occupancy. */
public final class OccupancyId {
    private final UUID value;

    private OccupancyId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static OccupancyId newId() {
        return new OccupancyId(UUID.randomUUID());
    }

    public static OccupancyId of(UUID value) {
        return new OccupancyId(value);
    }

    public static OccupancyId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new OccupancyId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OccupancyId other)) {
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

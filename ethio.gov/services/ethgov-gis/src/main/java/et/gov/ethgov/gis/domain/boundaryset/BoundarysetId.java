package et.gov.ethgov.gis.domain.boundaryset;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Boundaryset. */
public final class BoundarysetId {
    private final UUID value;

    private BoundarysetId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static BoundarysetId newId() {
        return new BoundarysetId(UUID.randomUUID());
    }

    public static BoundarysetId of(UUID value) {
        return new BoundarysetId(value);
    }

    public static BoundarysetId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new BoundarysetId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BoundarysetId other)) {
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

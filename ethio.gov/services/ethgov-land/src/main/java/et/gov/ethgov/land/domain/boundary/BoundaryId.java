package et.gov.ethgov.land.domain.boundary;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Boundary. */
public final class BoundaryId {
    private final UUID value;

    private BoundaryId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static BoundaryId newId() {
        return new BoundaryId(UUID.randomUUID());
    }

    public static BoundaryId of(UUID value) {
        return new BoundaryId(value);
    }

    public static BoundaryId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new BoundaryId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BoundaryId other)) {
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

package et.gov.ethgov.analytics.domain.cube;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Cube. */
public final class CubeId {
    private final UUID value;

    private CubeId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CubeId newId() {
        return new CubeId(UUID.randomUUID());
    }

    public static CubeId of(UUID value) {
        return new CubeId(value);
    }

    public static CubeId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CubeId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CubeId other)) {
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

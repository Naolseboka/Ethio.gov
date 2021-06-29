package et.gov.ethgov.gis.domain.feature;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Feature. */
public final class FeatureId {
    private final UUID value;

    private FeatureId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static FeatureId newId() {
        return new FeatureId(UUID.randomUUID());
    }

    public static FeatureId of(UUID value) {
        return new FeatureId(value);
    }

    public static FeatureId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new FeatureId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FeatureId other)) {
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

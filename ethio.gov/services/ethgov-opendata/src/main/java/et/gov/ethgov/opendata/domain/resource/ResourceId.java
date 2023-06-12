package et.gov.ethgov.opendata.domain.resource;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Resource. */
public final class ResourceId {
    private final UUID value;

    private ResourceId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ResourceId newId() {
        return new ResourceId(UUID.randomUUID());
    }

    public static ResourceId of(UUID value) {
        return new ResourceId(value);
    }

    public static ResourceId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ResourceId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceId other)) {
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

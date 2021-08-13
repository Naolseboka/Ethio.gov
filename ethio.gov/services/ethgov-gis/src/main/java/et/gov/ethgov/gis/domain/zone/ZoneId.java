package et.gov.ethgov.gis.domain.zone;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Zone. */
public final class ZoneId {
    private final UUID value;

    private ZoneId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ZoneId newId() {
        return new ZoneId(UUID.randomUUID());
    }

    public static ZoneId of(UUID value) {
        return new ZoneId(value);
    }

    public static ZoneId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ZoneId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ZoneId other)) {
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

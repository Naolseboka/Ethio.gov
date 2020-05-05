package et.gov.ethgov.directory.domain.facility;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Facility. */
public final class FacilityId {
    private final UUID value;

    private FacilityId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static FacilityId newId() {
        return new FacilityId(UUID.randomUUID());
    }

    public static FacilityId of(UUID value) {
        return new FacilityId(value);
    }

    public static FacilityId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new FacilityId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FacilityId other)) {
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

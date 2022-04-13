package et.gov.ethgov.land.domain.parcel;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Parcel. */
public final class ParcelId {
    private final UUID value;

    private ParcelId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ParcelId newId() {
        return new ParcelId(UUID.randomUUID());
    }

    public static ParcelId of(UUID value) {
        return new ParcelId(value);
    }

    public static ParcelId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ParcelId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParcelId other)) {
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

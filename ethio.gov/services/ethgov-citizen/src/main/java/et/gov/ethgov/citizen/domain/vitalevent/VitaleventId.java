package et.gov.ethgov.citizen.domain.vitalevent;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Vitalevent. */
public final class VitaleventId {
    private final UUID value;

    private VitaleventId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static VitaleventId newId() {
        return new VitaleventId(UUID.randomUUID());
    }

    public static VitaleventId of(UUID value) {
        return new VitaleventId(value);
    }

    public static VitaleventId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new VitaleventId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VitaleventId other)) {
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

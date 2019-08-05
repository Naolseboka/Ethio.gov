package et.gov.ethgov.citizen.domain.citizenship;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Citizenship. */
public final class CitizenshipId {
    private final UUID value;

    private CitizenshipId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CitizenshipId newId() {
        return new CitizenshipId(UUID.randomUUID());
    }

    public static CitizenshipId of(UUID value) {
        return new CitizenshipId(value);
    }

    public static CitizenshipId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CitizenshipId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CitizenshipId other)) {
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

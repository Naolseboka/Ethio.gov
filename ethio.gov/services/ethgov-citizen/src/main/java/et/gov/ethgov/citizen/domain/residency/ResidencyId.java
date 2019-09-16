package et.gov.ethgov.citizen.domain.residency;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Residency. */
public final class ResidencyId {
    private final UUID value;

    private ResidencyId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ResidencyId newId() {
        return new ResidencyId(UUID.randomUUID());
    }

    public static ResidencyId of(UUID value) {
        return new ResidencyId(value);
    }

    public static ResidencyId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ResidencyId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResidencyId other)) {
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

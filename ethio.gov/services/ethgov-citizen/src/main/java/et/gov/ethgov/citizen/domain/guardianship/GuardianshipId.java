package et.gov.ethgov.citizen.domain.guardianship;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Guardianship. */
public final class GuardianshipId {
    private final UUID value;

    private GuardianshipId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static GuardianshipId newId() {
        return new GuardianshipId(UUID.randomUUID());
    }

    public static GuardianshipId of(UUID value) {
        return new GuardianshipId(value);
    }

    public static GuardianshipId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new GuardianshipId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GuardianshipId other)) {
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

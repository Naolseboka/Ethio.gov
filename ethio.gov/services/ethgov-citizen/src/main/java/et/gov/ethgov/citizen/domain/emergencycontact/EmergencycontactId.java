package et.gov.ethgov.citizen.domain.emergencycontact;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Emergencycontact. */
public final class EmergencycontactId {
    private final UUID value;

    private EmergencycontactId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static EmergencycontactId newId() {
        return new EmergencycontactId(UUID.randomUUID());
    }

    public static EmergencycontactId of(UUID value) {
        return new EmergencycontactId(value);
    }

    public static EmergencycontactId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new EmergencycontactId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmergencycontactId other)) {
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

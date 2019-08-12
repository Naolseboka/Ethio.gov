package et.gov.ethgov.citizen.domain.disabilitystatus;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Disabilitystatus. */
public final class DisabilitystatusId {
    private final UUID value;

    private DisabilitystatusId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DisabilitystatusId newId() {
        return new DisabilitystatusId(UUID.randomUUID());
    }

    public static DisabilitystatusId of(UUID value) {
        return new DisabilitystatusId(value);
    }

    public static DisabilitystatusId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DisabilitystatusId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DisabilitystatusId other)) {
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

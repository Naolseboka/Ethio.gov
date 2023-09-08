package et.gov.ethgov.organization.domain.mandate;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Mandate. */
public final class MandateId {
    private final UUID value;

    private MandateId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static MandateId newId() {
        return new MandateId(UUID.randomUUID());
    }

    public static MandateId of(UUID value) {
        return new MandateId(value);
    }

    public static MandateId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new MandateId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MandateId other)) {
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

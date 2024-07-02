package et.gov.ethgov.permits.domain.permitchecklist;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Permitchecklist. */
public final class PermitchecklistId {
    private final UUID value;

    private PermitchecklistId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PermitchecklistId newId() {
        return new PermitchecklistId(UUID.randomUUID());
    }

    public static PermitchecklistId of(UUID value) {
        return new PermitchecklistId(value);
    }

    public static PermitchecklistId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PermitchecklistId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermitchecklistId other)) {
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

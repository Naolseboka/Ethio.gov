package et.gov.ethgov.permits.domain.permitcondition;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Permitcondition. */
public final class PermitconditionId {
    private final UUID value;

    private PermitconditionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PermitconditionId newId() {
        return new PermitconditionId(UUID.randomUUID());
    }

    public static PermitconditionId of(UUID value) {
        return new PermitconditionId(value);
    }

    public static PermitconditionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PermitconditionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermitconditionId other)) {
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

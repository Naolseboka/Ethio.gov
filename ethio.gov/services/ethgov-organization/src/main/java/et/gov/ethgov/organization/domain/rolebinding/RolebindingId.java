package et.gov.ethgov.organization.domain.rolebinding;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Rolebinding. */
public final class RolebindingId {
    private final UUID value;

    private RolebindingId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static RolebindingId newId() {
        return new RolebindingId(UUID.randomUUID());
    }

    public static RolebindingId of(UUID value) {
        return new RolebindingId(value);
    }

    public static RolebindingId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new RolebindingId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RolebindingId other)) {
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

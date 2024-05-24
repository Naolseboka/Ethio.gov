package et.gov.ethgov.permits.domain.bond;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Bond. */
public final class BondId {
    private final UUID value;

    private BondId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static BondId newId() {
        return new BondId(UUID.randomUUID());
    }

    public static BondId of(UUID value) {
        return new BondId(value);
    }

    public static BondId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new BondId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BondId other)) {
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

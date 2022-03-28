package et.gov.ethgov.land.domain.encumbrance;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Encumbrance. */
public final class EncumbranceId {
    private final UUID value;

    private EncumbranceId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static EncumbranceId newId() {
        return new EncumbranceId(UUID.randomUUID());
    }

    public static EncumbranceId of(UUID value) {
        return new EncumbranceId(value);
    }

    public static EncumbranceId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new EncumbranceId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EncumbranceId other)) {
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

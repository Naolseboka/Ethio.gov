package et.gov.ethgov.land.domain.easement;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Easement. */
public final class EasementId {
    private final UUID value;

    private EasementId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static EasementId newId() {
        return new EasementId(UUID.randomUUID());
    }

    public static EasementId of(UUID value) {
        return new EasementId(value);
    }

    public static EasementId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new EasementId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EasementId other)) {
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

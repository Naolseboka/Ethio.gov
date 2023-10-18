package et.gov.ethgov.organization.domain.seal;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Seal. */
public final class SealId {
    private final UUID value;

    private SealId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SealId newId() {
        return new SealId(UUID.randomUUID());
    }

    public static SealId of(UUID value) {
        return new SealId(value);
    }

    public static SealId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SealId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SealId other)) {
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

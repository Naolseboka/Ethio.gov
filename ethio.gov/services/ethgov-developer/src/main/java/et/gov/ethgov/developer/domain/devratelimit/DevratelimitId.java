package et.gov.ethgov.developer.domain.devratelimit;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Devratelimit. */
public final class DevratelimitId {
    private final UUID value;

    private DevratelimitId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DevratelimitId newId() {
        return new DevratelimitId(UUID.randomUUID());
    }

    public static DevratelimitId of(UUID value) {
        return new DevratelimitId(value);
    }

    public static DevratelimitId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DevratelimitId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DevratelimitId other)) {
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

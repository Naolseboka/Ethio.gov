package et.gov.ethgov.search.domain.boost;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Boost. */
public final class BoostId {
    private final UUID value;

    private BoostId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static BoostId newId() {
        return new BoostId(UUID.randomUUID());
    }

    public static BoostId of(UUID value) {
        return new BoostId(value);
    }

    public static BoostId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new BoostId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BoostId other)) {
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

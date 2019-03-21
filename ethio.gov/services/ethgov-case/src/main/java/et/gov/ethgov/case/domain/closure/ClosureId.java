package et.gov.ethgov.case.domain.closure;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Closure. */
public final class ClosureId {
    private final UUID value;

    private ClosureId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ClosureId newId() {
        return new ClosureId(UUID.randomUUID());
    }

    public static ClosureId of(UUID value) {
        return new ClosureId(value);
    }

    public static ClosureId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ClosureId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClosureId other)) {
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

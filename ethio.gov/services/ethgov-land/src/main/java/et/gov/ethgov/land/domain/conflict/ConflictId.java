package et.gov.ethgov.land.domain.conflict;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Conflict. */
public final class ConflictId {
    private final UUID value;

    private ConflictId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ConflictId newId() {
        return new ConflictId(UUID.randomUUID());
    }

    public static ConflictId of(UUID value) {
        return new ConflictId(value);
    }

    public static ConflictId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ConflictId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConflictId other)) {
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

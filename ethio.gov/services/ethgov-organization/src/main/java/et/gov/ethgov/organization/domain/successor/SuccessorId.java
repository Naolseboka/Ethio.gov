package et.gov.ethgov.organization.domain.successor;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Successor. */
public final class SuccessorId {
    private final UUID value;

    private SuccessorId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SuccessorId newId() {
        return new SuccessorId(UUID.randomUUID());
    }

    public static SuccessorId of(UUID value) {
        return new SuccessorId(value);
    }

    public static SuccessorId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SuccessorId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SuccessorId other)) {
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

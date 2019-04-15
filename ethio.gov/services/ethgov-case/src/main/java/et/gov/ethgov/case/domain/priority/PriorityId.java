package et.gov.ethgov.case.domain.priority;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Priority. */
public final class PriorityId {
    private final UUID value;

    private PriorityId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PriorityId newId() {
        return new PriorityId(UUID.randomUUID());
    }

    public static PriorityId of(UUID value) {
        return new PriorityId(value);
    }

    public static PriorityId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PriorityId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PriorityId other)) {
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

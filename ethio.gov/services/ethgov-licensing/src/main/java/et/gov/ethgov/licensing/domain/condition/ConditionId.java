package et.gov.ethgov.licensing.domain.condition;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Condition. */
public final class ConditionId {
    private final UUID value;

    private ConditionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ConditionId newId() {
        return new ConditionId(UUID.randomUUID());
    }

    public static ConditionId of(UUID value) {
        return new ConditionId(value);
    }

    public static ConditionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ConditionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConditionId other)) {
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

package et.gov.ethgov.case.domain.decision;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Decision. */
public final class DecisionId {
    private final UUID value;

    private DecisionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DecisionId newId() {
        return new DecisionId(UUID.randomUUID());
    }

    public static DecisionId of(UUID value) {
        return new DecisionId(value);
    }

    public static DecisionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DecisionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DecisionId other)) {
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

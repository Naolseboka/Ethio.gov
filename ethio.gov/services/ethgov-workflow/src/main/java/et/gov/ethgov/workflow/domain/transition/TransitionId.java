package et.gov.ethgov.workflow.domain.transition;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Transition. */
public final class TransitionId {
    private final UUID value;

    private TransitionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static TransitionId newId() {
        return new TransitionId(UUID.randomUUID());
    }

    public static TransitionId of(UUID value) {
        return new TransitionId(value);
    }

    public static TransitionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new TransitionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransitionId other)) {
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

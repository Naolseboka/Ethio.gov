package et.gov.ethgov.workflow.domain.escalation;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Escalation. */
public final class EscalationId {
    private final UUID value;

    private EscalationId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static EscalationId newId() {
        return new EscalationId(UUID.randomUUID());
    }

    public static EscalationId of(UUID value) {
        return new EscalationId(value);
    }

    public static EscalationId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new EscalationId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EscalationId other)) {
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

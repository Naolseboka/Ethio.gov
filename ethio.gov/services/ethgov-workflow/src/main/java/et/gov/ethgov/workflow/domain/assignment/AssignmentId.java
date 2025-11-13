package et.gov.ethgov.workflow.domain.assignment;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Assignment. */
public final class AssignmentId {
    private final UUID value;

    private AssignmentId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static AssignmentId newId() {
        return new AssignmentId(UUID.randomUUID());
    }

    public static AssignmentId of(UUID value) {
        return new AssignmentId(value);
    }

    public static AssignmentId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new AssignmentId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssignmentId other)) {
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

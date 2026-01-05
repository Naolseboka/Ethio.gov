package et.gov.ethgov.workflow.domain.task;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Task. */
public final class TaskId {
    private final UUID value;

    private TaskId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static TaskId newId() {
        return new TaskId(UUID.randomUUID());
    }

    public static TaskId of(UUID value) {
        return new TaskId(value);
    }

    public static TaskId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new TaskId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskId other)) {
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

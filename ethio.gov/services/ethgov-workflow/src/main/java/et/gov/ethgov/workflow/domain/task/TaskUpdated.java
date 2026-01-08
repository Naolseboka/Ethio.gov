package et.gov.ethgov.workflow.domain.task;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TaskUpdated. */
public final class TaskUpdated {
    private final TaskId id;
    private final Instant occurredAt;

    public TaskUpdated(TaskId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public TaskId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.workflow.domain.task;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TaskSuspended. */
public final class TaskSuspended {
    private final TaskId id;
    private final Instant occurredAt;

    public TaskSuspended(TaskId id, Instant occurredAt) {
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

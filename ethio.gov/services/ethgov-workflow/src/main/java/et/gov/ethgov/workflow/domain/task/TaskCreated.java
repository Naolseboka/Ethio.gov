package et.gov.ethgov.workflow.domain.task;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TaskCreated. */
public final class TaskCreated {
    private final TaskId id;
    private final Instant occurredAt;

    public TaskCreated(TaskId id, Instant occurredAt) {
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

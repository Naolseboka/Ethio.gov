package et.gov.ethgov.workflow.domain.assignment;

import java.time.Instant;
import java.util.Objects;

/** Domain event: AssignmentClosed. */
public final class AssignmentClosed {
    private final AssignmentId id;
    private final Instant occurredAt;

    public AssignmentClosed(AssignmentId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public AssignmentId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

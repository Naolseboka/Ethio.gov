package et.gov.ethgov.workflow.domain.checklist;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ChecklistActivated. */
public final class ChecklistActivated {
    private final ChecklistId id;
    private final Instant occurredAt;

    public ChecklistActivated(ChecklistId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ChecklistId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

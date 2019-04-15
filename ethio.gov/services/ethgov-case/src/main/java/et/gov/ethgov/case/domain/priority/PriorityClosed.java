package et.gov.ethgov.case.domain.priority;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PriorityClosed. */
public final class PriorityClosed {
    private final PriorityId id;
    private final Instant occurredAt;

    public PriorityClosed(PriorityId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PriorityId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

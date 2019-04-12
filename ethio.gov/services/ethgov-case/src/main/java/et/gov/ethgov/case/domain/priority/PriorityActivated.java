package et.gov.ethgov.case.domain.priority;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PriorityActivated. */
public final class PriorityActivated {
    private final PriorityId id;
    private final Instant occurredAt;

    public PriorityActivated(PriorityId id, Instant occurredAt) {
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

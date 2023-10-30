package et.gov.ethgov.organization.domain.successor;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SuccessorCreated. */
public final class SuccessorCreated {
    private final SuccessorId id;
    private final Instant occurredAt;

    public SuccessorCreated(SuccessorId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SuccessorId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

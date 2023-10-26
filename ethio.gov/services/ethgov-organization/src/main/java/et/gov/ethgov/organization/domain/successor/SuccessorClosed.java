package et.gov.ethgov.organization.domain.successor;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SuccessorClosed. */
public final class SuccessorClosed {
    private final SuccessorId id;
    private final Instant occurredAt;

    public SuccessorClosed(SuccessorId id, Instant occurredAt) {
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

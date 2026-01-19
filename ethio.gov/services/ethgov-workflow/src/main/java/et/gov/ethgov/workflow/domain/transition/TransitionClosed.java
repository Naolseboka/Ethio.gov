package et.gov.ethgov.workflow.domain.transition;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TransitionClosed. */
public final class TransitionClosed {
    private final TransitionId id;
    private final Instant occurredAt;

    public TransitionClosed(TransitionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public TransitionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

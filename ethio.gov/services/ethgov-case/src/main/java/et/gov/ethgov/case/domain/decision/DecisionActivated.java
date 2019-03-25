package et.gov.ethgov.case.domain.decision;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DecisionActivated. */
public final class DecisionActivated {
    private final DecisionId id;
    private final Instant occurredAt;

    public DecisionActivated(DecisionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DecisionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

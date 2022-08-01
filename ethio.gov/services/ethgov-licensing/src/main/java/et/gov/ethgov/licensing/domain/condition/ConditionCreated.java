package et.gov.ethgov.licensing.domain.condition;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ConditionCreated. */
public final class ConditionCreated {
    private final ConditionId id;
    private final Instant occurredAt;

    public ConditionCreated(ConditionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ConditionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

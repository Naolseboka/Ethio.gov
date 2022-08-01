package et.gov.ethgov.licensing.domain.condition;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ConditionActivated. */
public final class ConditionActivated {
    private final ConditionId id;
    private final Instant occurredAt;

    public ConditionActivated(ConditionId id, Instant occurredAt) {
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

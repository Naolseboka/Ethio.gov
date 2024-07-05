package et.gov.ethgov.permits.domain.permitcondition;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PermitconditionCreated. */
public final class PermitconditionCreated {
    private final PermitconditionId id;
    private final Instant occurredAt;

    public PermitconditionCreated(PermitconditionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PermitconditionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

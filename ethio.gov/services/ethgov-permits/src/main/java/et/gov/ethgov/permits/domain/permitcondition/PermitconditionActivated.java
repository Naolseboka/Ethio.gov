package et.gov.ethgov.permits.domain.permitcondition;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PermitconditionActivated. */
public final class PermitconditionActivated {
    private final PermitconditionId id;
    private final Instant occurredAt;

    public PermitconditionActivated(PermitconditionId id, Instant occurredAt) {
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

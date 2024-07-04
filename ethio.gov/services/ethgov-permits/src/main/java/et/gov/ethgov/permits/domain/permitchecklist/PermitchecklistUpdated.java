package et.gov.ethgov.permits.domain.permitchecklist;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PermitchecklistUpdated. */
public final class PermitchecklistUpdated {
    private final PermitchecklistId id;
    private final Instant occurredAt;

    public PermitchecklistUpdated(PermitchecklistId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PermitchecklistId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

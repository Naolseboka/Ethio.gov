package et.gov.ethgov.land.domain.lease;

import java.time.Instant;
import java.util.Objects;

/** Domain event: LeaseCreated. */
public final class LeaseCreated {
    private final LeaseId id;
    private final Instant occurredAt;

    public LeaseCreated(LeaseId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public LeaseId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

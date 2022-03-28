package et.gov.ethgov.land.domain.lease;

import java.time.Instant;
import java.util.Objects;

/** Domain event: LeaseActivated. */
public final class LeaseActivated {
    private final LeaseId id;
    private final Instant occurredAt;

    public LeaseActivated(LeaseId id, Instant occurredAt) {
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

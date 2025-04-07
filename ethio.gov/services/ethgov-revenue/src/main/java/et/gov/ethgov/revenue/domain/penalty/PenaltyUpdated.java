package et.gov.ethgov.revenue.domain.penalty;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PenaltyUpdated. */
public final class PenaltyUpdated {
    private final PenaltyId id;
    private final Instant occurredAt;

    public PenaltyUpdated(PenaltyId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PenaltyId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

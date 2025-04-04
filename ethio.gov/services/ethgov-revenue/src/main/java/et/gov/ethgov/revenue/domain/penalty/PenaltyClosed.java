package et.gov.ethgov.revenue.domain.penalty;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PenaltyClosed. */
public final class PenaltyClosed {
    private final PenaltyId id;
    private final Instant occurredAt;

    public PenaltyClosed(PenaltyId id, Instant occurredAt) {
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

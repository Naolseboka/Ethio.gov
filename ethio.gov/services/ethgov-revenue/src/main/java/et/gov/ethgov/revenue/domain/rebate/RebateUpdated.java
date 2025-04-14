package et.gov.ethgov.revenue.domain.rebate;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RebateUpdated. */
public final class RebateUpdated {
    private final RebateId id;
    private final Instant occurredAt;

    public RebateUpdated(RebateId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public RebateId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

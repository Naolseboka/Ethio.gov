package et.gov.ethgov.revenue.domain.rebate;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RebateCreated. */
public final class RebateCreated {
    private final RebateId id;
    private final Instant occurredAt;

    public RebateCreated(RebateId id, Instant occurredAt) {
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

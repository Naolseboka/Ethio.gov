package et.gov.ethgov.revenue.domain.revenueappeal;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RevenueappealSuspended. */
public final class RevenueappealSuspended {
    private final RevenueappealId id;
    private final Instant occurredAt;

    public RevenueappealSuspended(RevenueappealId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public RevenueappealId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

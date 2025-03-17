package et.gov.ethgov.revenue.domain.exemption;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ExemptionSuspended. */
public final class ExemptionSuspended {
    private final ExemptionId id;
    private final Instant occurredAt;

    public ExemptionSuspended(ExemptionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ExemptionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

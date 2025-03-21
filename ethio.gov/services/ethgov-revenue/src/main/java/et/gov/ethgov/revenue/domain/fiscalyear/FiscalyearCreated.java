package et.gov.ethgov.revenue.domain.fiscalyear;

import java.time.Instant;
import java.util.Objects;

/** Domain event: FiscalyearCreated. */
public final class FiscalyearCreated {
    private final FiscalyearId id;
    private final Instant occurredAt;

    public FiscalyearCreated(FiscalyearId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public FiscalyearId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.revenue.domain.ledger;

import java.time.Instant;
import java.util.Objects;

/** Domain event: LedgerSuspended. */
public final class LedgerSuspended {
    private final LedgerId id;
    private final Instant occurredAt;

    public LedgerSuspended(LedgerId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public LedgerId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

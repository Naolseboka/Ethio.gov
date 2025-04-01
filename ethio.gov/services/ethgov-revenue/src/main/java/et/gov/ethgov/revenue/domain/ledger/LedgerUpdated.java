package et.gov.ethgov.revenue.domain.ledger;

import java.time.Instant;
import java.util.Objects;

/** Domain event: LedgerUpdated. */
public final class LedgerUpdated {
    private final LedgerId id;
    private final Instant occurredAt;

    public LedgerUpdated(LedgerId id, Instant occurredAt) {
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

package et.gov.ethgov.revenue.domain.ledger;

import java.time.Instant;
import java.util.Objects;

/** Domain event: LedgerClosed. */
public final class LedgerClosed {
    private final LedgerId id;
    private final Instant occurredAt;

    public LedgerClosed(LedgerId id, Instant occurredAt) {
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

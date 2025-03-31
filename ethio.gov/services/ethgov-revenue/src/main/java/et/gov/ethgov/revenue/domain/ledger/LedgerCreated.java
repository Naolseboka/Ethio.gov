package et.gov.ethgov.revenue.domain.ledger;

import java.time.Instant;
import java.util.Objects;

/** Domain event: LedgerCreated. */
public final class LedgerCreated {
    private final LedgerId id;
    private final Instant occurredAt;

    public LedgerCreated(LedgerId id, Instant occurredAt) {
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

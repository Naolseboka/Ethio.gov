package et.gov.ethgov.payments.domain.reconciliation;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ReconciliationActivated. */
public final class ReconciliationActivated {
    private final ReconciliationId id;
    private final Instant occurredAt;

    public ReconciliationActivated(ReconciliationId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ReconciliationId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

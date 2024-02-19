package et.gov.ethgov.payments.domain.reconciliation;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ReconciliationCreated. */
public final class ReconciliationCreated {
    private final ReconciliationId id;
    private final Instant occurredAt;

    public ReconciliationCreated(ReconciliationId id, Instant occurredAt) {
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

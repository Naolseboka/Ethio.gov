package et.gov.ethgov.payments.domain.receipt;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ReceiptSuspended. */
public final class ReceiptSuspended {
    private final ReceiptId id;
    private final Instant occurredAt;

    public ReceiptSuspended(ReceiptId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ReceiptId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

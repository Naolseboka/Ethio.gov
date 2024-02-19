package et.gov.ethgov.payments.domain.receipt;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ReceiptUpdated. */
public final class ReceiptUpdated {
    private final ReceiptId id;
    private final Instant occurredAt;

    public ReceiptUpdated(ReceiptId id, Instant occurredAt) {
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

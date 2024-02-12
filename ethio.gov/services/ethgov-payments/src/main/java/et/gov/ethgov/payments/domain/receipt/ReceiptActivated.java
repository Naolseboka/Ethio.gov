package et.gov.ethgov.payments.domain.receipt;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ReceiptActivated. */
public final class ReceiptActivated {
    private final ReceiptId id;
    private final Instant occurredAt;

    public ReceiptActivated(ReceiptId id, Instant occurredAt) {
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

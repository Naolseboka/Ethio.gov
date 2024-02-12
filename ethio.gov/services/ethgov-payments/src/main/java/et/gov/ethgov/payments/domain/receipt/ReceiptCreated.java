package et.gov.ethgov.payments.domain.receipt;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ReceiptCreated. */
public final class ReceiptCreated {
    private final ReceiptId id;
    private final Instant occurredAt;

    public ReceiptCreated(ReceiptId id, Instant occurredAt) {
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

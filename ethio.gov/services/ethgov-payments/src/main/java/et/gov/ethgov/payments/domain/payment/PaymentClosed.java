package et.gov.ethgov.payments.domain.payment;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PaymentClosed. */
public final class PaymentClosed {
    private final PaymentId id;
    private final Instant occurredAt;

    public PaymentClosed(PaymentId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PaymentId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

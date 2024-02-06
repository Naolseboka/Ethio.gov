package et.gov.ethgov.payments.domain.payment;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PaymentSuspended. */
public final class PaymentSuspended {
    private final PaymentId id;
    private final Instant occurredAt;

    public PaymentSuspended(PaymentId id, Instant occurredAt) {
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

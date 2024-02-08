package et.gov.ethgov.payments.domain.paymentplan;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PaymentplanClosed. */
public final class PaymentplanClosed {
    private final PaymentplanId id;
    private final Instant occurredAt;

    public PaymentplanClosed(PaymentplanId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PaymentplanId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.payments.domain.paymentplan;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PaymentplanUpdated. */
public final class PaymentplanUpdated {
    private final PaymentplanId id;
    private final Instant occurredAt;

    public PaymentplanUpdated(PaymentplanId id, Instant occurredAt) {
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

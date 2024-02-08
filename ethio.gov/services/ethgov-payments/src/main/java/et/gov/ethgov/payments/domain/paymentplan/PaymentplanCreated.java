package et.gov.ethgov.payments.domain.paymentplan;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PaymentplanCreated. */
public final class PaymentplanCreated {
    private final PaymentplanId id;
    private final Instant occurredAt;

    public PaymentplanCreated(PaymentplanId id, Instant occurredAt) {
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

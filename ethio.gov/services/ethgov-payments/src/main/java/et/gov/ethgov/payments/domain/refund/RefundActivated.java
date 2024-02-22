package et.gov.ethgov.payments.domain.refund;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RefundActivated. */
public final class RefundActivated {
    private final RefundId id;
    private final Instant occurredAt;

    public RefundActivated(RefundId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public RefundId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

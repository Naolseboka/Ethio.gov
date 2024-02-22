package et.gov.ethgov.payments.domain.refund;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RefundClosed. */
public final class RefundClosed {
    private final RefundId id;
    private final Instant occurredAt;

    public RefundClosed(RefundId id, Instant occurredAt) {
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

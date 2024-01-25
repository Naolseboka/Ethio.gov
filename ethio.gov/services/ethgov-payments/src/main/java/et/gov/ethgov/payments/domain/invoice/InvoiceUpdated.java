package et.gov.ethgov.payments.domain.invoice;

import java.time.Instant;
import java.util.Objects;

/** Domain event: InvoiceUpdated. */
public final class InvoiceUpdated {
    private final InvoiceId id;
    private final Instant occurredAt;

    public InvoiceUpdated(InvoiceId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public InvoiceId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

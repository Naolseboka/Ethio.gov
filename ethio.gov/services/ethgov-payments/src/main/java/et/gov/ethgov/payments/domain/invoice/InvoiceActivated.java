package et.gov.ethgov.payments.domain.invoice;

import java.time.Instant;
import java.util.Objects;

/** Domain event: InvoiceActivated. */
public final class InvoiceActivated {
    private final InvoiceId id;
    private final Instant occurredAt;

    public InvoiceActivated(InvoiceId id, Instant occurredAt) {
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

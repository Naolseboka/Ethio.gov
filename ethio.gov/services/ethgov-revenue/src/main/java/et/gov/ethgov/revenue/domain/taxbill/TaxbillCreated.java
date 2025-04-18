package et.gov.ethgov.revenue.domain.taxbill;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TaxbillCreated. */
public final class TaxbillCreated {
    private final TaxbillId id;
    private final Instant occurredAt;

    public TaxbillCreated(TaxbillId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public TaxbillId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

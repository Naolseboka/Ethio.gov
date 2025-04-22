package et.gov.ethgov.revenue.domain.taxbill;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TaxbillUpdated. */
public final class TaxbillUpdated {
    private final TaxbillId id;
    private final Instant occurredAt;

    public TaxbillUpdated(TaxbillId id, Instant occurredAt) {
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

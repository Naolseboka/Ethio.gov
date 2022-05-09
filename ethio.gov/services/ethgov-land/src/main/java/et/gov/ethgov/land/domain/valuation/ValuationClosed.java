package et.gov.ethgov.land.domain.valuation;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ValuationClosed. */
public final class ValuationClosed {
    private final ValuationId id;
    private final Instant occurredAt;

    public ValuationClosed(ValuationId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ValuationId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.payments.domain.settlement;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SettlementActivated. */
public final class SettlementActivated {
    private final SettlementId id;
    private final Instant occurredAt;

    public SettlementActivated(SettlementId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SettlementId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

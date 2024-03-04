package et.gov.ethgov.payments.domain.settlement;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SettlementSuspended. */
public final class SettlementSuspended {
    private final SettlementId id;
    private final Instant occurredAt;

    public SettlementSuspended(SettlementId id, Instant occurredAt) {
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

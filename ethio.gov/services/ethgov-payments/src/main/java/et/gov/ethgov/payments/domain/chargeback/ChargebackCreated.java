package et.gov.ethgov.payments.domain.chargeback;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ChargebackCreated. */
public final class ChargebackCreated {
    private final ChargebackId id;
    private final Instant occurredAt;

    public ChargebackCreated(ChargebackId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ChargebackId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

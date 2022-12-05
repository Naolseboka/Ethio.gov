package et.gov.ethgov.notification.domain.delivery;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DeliverySuspended. */
public final class DeliverySuspended {
    private final DeliveryId id;
    private final Instant occurredAt;

    public DeliverySuspended(DeliveryId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DeliveryId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

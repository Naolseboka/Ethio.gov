package et.gov.ethgov.notification.domain.delivery;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DeliveryActivated. */
public final class DeliveryActivated {
    private final DeliveryId id;
    private final Instant occurredAt;

    public DeliveryActivated(DeliveryId id, Instant occurredAt) {
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

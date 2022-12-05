package et.gov.ethgov.notification.domain.delivery;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DeliveryCreated. */
public final class DeliveryCreated {
    private final DeliveryId id;
    private final Instant occurredAt;

    public DeliveryCreated(DeliveryId id, Instant occurredAt) {
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

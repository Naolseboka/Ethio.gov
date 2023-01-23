package et.gov.ethgov.notification.domain.subscription;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SubscriptionCreated. */
public final class SubscriptionCreated {
    private final SubscriptionId id;
    private final Instant occurredAt;

    public SubscriptionCreated(SubscriptionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SubscriptionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.notification.domain.notifywebhook;

import java.time.Instant;
import java.util.Objects;

/** Domain event: NotifywebhookUpdated. */
public final class NotifywebhookUpdated {
    private final NotifywebhookId id;
    private final Instant occurredAt;

    public NotifywebhookUpdated(NotifywebhookId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public NotifywebhookId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

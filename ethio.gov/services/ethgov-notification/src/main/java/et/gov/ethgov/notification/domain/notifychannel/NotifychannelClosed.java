package et.gov.ethgov.notification.domain.notifychannel;

import java.time.Instant;
import java.util.Objects;

/** Domain event: NotifychannelClosed. */
public final class NotifychannelClosed {
    private final NotifychannelId id;
    private final Instant occurredAt;

    public NotifychannelClosed(NotifychannelId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public NotifychannelId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

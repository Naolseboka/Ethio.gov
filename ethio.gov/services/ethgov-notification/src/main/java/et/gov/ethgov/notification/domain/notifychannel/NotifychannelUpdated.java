package et.gov.ethgov.notification.domain.notifychannel;

import java.time.Instant;
import java.util.Objects;

/** Domain event: NotifychannelUpdated. */
public final class NotifychannelUpdated {
    private final NotifychannelId id;
    private final Instant occurredAt;

    public NotifychannelUpdated(NotifychannelId id, Instant occurredAt) {
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

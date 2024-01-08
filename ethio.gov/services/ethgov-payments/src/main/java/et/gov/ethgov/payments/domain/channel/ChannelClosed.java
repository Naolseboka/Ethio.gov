package et.gov.ethgov.payments.domain.channel;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ChannelClosed. */
public final class ChannelClosed {
    private final ChannelId id;
    private final Instant occurredAt;

    public ChannelClosed(ChannelId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ChannelId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

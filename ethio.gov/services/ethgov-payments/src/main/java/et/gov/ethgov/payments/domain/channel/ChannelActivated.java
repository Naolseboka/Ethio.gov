package et.gov.ethgov.payments.domain.channel;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ChannelActivated. */
public final class ChannelActivated {
    private final ChannelId id;
    private final Instant occurredAt;

    public ChannelActivated(ChannelId id, Instant occurredAt) {
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

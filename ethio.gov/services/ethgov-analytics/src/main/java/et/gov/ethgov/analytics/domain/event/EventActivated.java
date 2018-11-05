package et.gov.ethgov.analytics.domain.event;

import java.time.Instant;
import java.util.Objects;

/** Domain event: EventActivated. */
public final class EventActivated {
    private final EventId id;
    private final Instant occurredAt;

    public EventActivated(EventId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public EventId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

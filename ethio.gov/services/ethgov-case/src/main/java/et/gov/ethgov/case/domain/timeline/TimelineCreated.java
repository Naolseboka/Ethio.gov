package et.gov.ethgov.case.domain.timeline;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TimelineCreated. */
public final class TimelineCreated {
    private final TimelineId id;
    private final Instant occurredAt;

    public TimelineCreated(TimelineId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public TimelineId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

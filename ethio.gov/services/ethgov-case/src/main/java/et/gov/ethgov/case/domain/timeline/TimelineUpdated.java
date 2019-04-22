package et.gov.ethgov.case.domain.timeline;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TimelineUpdated. */
public final class TimelineUpdated {
    private final TimelineId id;
    private final Instant occurredAt;

    public TimelineUpdated(TimelineId id, Instant occurredAt) {
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

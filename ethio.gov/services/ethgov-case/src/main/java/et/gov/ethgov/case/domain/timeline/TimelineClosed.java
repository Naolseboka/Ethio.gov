package et.gov.ethgov.case.domain.timeline;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TimelineClosed. */
public final class TimelineClosed {
    private final TimelineId id;
    private final Instant occurredAt;

    public TimelineClosed(TimelineId id, Instant occurredAt) {
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

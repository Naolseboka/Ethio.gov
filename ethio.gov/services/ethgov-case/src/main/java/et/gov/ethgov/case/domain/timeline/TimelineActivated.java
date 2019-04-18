package et.gov.ethgov.case.domain.timeline;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TimelineActivated. */
public final class TimelineActivated {
    private final TimelineId id;
    private final Instant occurredAt;

    public TimelineActivated(TimelineId id, Instant occurredAt) {
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

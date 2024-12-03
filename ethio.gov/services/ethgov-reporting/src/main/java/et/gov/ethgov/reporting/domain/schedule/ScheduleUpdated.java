package et.gov.ethgov.reporting.domain.schedule;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ScheduleUpdated. */
public final class ScheduleUpdated {
    private final ScheduleId id;
    private final Instant occurredAt;

    public ScheduleUpdated(ScheduleId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ScheduleId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

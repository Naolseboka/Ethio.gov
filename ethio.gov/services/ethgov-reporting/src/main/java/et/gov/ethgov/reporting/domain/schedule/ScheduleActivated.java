package et.gov.ethgov.reporting.domain.schedule;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ScheduleActivated. */
public final class ScheduleActivated {
    private final ScheduleId id;
    private final Instant occurredAt;

    public ScheduleActivated(ScheduleId id, Instant occurredAt) {
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

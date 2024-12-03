package et.gov.ethgov.reporting.domain.schedule;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ScheduleSuspended. */
public final class ScheduleSuspended {
    private final ScheduleId id;
    private final Instant occurredAt;

    public ScheduleSuspended(ScheduleId id, Instant occurredAt) {
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

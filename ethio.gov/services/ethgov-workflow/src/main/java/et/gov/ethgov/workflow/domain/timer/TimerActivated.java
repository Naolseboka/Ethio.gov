package et.gov.ethgov.workflow.domain.timer;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TimerActivated. */
public final class TimerActivated {
    private final TimerId id;
    private final Instant occurredAt;

    public TimerActivated(TimerId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public TimerId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

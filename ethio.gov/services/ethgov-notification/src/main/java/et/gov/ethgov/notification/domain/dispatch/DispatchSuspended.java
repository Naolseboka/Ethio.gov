package et.gov.ethgov.notification.domain.dispatch;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DispatchSuspended. */
public final class DispatchSuspended {
    private final DispatchId id;
    private final Instant occurredAt;

    public DispatchSuspended(DispatchId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DispatchId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

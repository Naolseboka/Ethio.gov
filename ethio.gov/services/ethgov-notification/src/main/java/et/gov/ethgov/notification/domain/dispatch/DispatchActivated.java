package et.gov.ethgov.notification.domain.dispatch;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DispatchActivated. */
public final class DispatchActivated {
    private final DispatchId id;
    private final Instant occurredAt;

    public DispatchActivated(DispatchId id, Instant occurredAt) {
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

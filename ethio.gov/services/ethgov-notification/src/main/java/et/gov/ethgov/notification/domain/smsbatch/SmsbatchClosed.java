package et.gov.ethgov.notification.domain.smsbatch;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SmsbatchClosed. */
public final class SmsbatchClosed {
    private final SmsbatchId id;
    private final Instant occurredAt;

    public SmsbatchClosed(SmsbatchId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SmsbatchId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

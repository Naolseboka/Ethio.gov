package et.gov.ethgov.notification.domain.smsbatch;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SmsbatchCreated. */
public final class SmsbatchCreated {
    private final SmsbatchId id;
    private final Instant occurredAt;

    public SmsbatchCreated(SmsbatchId id, Instant occurredAt) {
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

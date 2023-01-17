package et.gov.ethgov.notification.domain.smsbatch;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SmsbatchActivated. */
public final class SmsbatchActivated {
    private final SmsbatchId id;
    private final Instant occurredAt;

    public SmsbatchActivated(SmsbatchId id, Instant occurredAt) {
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

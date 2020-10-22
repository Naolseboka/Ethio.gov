package et.gov.ethgov.document.domain.retention;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RetentionSuspended. */
public final class RetentionSuspended {
    private final RetentionId id;
    private final Instant occurredAt;

    public RetentionSuspended(RetentionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public RetentionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

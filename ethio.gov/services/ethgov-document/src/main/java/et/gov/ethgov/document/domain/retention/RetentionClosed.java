package et.gov.ethgov.document.domain.retention;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RetentionClosed. */
public final class RetentionClosed {
    private final RetentionId id;
    private final Instant occurredAt;

    public RetentionClosed(RetentionId id, Instant occurredAt) {
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

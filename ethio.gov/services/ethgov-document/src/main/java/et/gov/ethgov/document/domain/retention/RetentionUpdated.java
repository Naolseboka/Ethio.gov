package et.gov.ethgov.document.domain.retention;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RetentionUpdated. */
public final class RetentionUpdated {
    private final RetentionId id;
    private final Instant occurredAt;

    public RetentionUpdated(RetentionId id, Instant occurredAt) {
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

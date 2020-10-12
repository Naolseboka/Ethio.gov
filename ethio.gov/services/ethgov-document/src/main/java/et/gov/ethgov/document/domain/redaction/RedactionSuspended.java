package et.gov.ethgov.document.domain.redaction;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RedactionSuspended. */
public final class RedactionSuspended {
    private final RedactionId id;
    private final Instant occurredAt;

    public RedactionSuspended(RedactionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public RedactionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

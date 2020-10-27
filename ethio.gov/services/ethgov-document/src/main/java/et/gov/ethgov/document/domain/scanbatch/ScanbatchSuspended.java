package et.gov.ethgov.document.domain.scanbatch;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ScanbatchSuspended. */
public final class ScanbatchSuspended {
    private final ScanbatchId id;
    private final Instant occurredAt;

    public ScanbatchSuspended(ScanbatchId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ScanbatchId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.document.domain.scanbatch;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ScanbatchUpdated. */
public final class ScanbatchUpdated {
    private final ScanbatchId id;
    private final Instant occurredAt;

    public ScanbatchUpdated(ScanbatchId id, Instant occurredAt) {
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

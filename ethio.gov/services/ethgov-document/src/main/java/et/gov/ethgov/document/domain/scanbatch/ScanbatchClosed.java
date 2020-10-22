package et.gov.ethgov.document.domain.scanbatch;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ScanbatchClosed. */
public final class ScanbatchClosed {
    private final ScanbatchId id;
    private final Instant occurredAt;

    public ScanbatchClosed(ScanbatchId id, Instant occurredAt) {
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

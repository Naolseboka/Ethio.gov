package et.gov.ethgov.revenue.domain.writeoff;

import java.time.Instant;
import java.util.Objects;

/** Domain event: WriteoffActivated. */
public final class WriteoffActivated {
    private final WriteoffId id;
    private final Instant occurredAt;

    public WriteoffActivated(WriteoffId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public WriteoffId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

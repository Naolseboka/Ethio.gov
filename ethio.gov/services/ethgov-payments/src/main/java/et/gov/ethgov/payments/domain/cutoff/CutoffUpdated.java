package et.gov.ethgov.payments.domain.cutoff;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CutoffUpdated. */
public final class CutoffUpdated {
    private final CutoffId id;
    private final Instant occurredAt;

    public CutoffUpdated(CutoffId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CutoffId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

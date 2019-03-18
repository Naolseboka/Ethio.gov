package et.gov.ethgov.case.domain.closure;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ClosureActivated. */
public final class ClosureActivated {
    private final ClosureId id;
    private final Instant occurredAt;

    public ClosureActivated(ClosureId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ClosureId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

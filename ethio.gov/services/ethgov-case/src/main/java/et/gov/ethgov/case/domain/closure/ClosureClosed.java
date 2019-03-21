package et.gov.ethgov.case.domain.closure;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ClosureClosed. */
public final class ClosureClosed {
    private final ClosureId id;
    private final Instant occurredAt;

    public ClosureClosed(ClosureId id, Instant occurredAt) {
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

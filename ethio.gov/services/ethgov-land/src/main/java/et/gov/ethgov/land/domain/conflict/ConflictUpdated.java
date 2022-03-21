package et.gov.ethgov.land.domain.conflict;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ConflictUpdated. */
public final class ConflictUpdated {
    private final ConflictId id;
    private final Instant occurredAt;

    public ConflictUpdated(ConflictId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ConflictId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

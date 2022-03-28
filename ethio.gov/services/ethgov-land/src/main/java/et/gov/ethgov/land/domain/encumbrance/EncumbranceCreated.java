package et.gov.ethgov.land.domain.encumbrance;

import java.time.Instant;
import java.util.Objects;

/** Domain event: EncumbranceCreated. */
public final class EncumbranceCreated {
    private final EncumbranceId id;
    private final Instant occurredAt;

    public EncumbranceCreated(EncumbranceId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public EncumbranceId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

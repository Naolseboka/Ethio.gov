package et.gov.ethgov.land.domain.easement;

import java.time.Instant;
import java.util.Objects;

/** Domain event: EasementSuspended. */
public final class EasementSuspended {
    private final EasementId id;
    private final Instant occurredAt;

    public EasementSuspended(EasementId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public EasementId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

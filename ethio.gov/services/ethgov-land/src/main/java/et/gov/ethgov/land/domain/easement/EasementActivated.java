package et.gov.ethgov.land.domain.easement;

import java.time.Instant;
import java.util.Objects;

/** Domain event: EasementActivated. */
public final class EasementActivated {
    private final EasementId id;
    private final Instant occurredAt;

    public EasementActivated(EasementId id, Instant occurredAt) {
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

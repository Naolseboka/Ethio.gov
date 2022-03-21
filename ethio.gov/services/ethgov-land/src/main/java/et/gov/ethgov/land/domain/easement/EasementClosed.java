package et.gov.ethgov.land.domain.easement;

import java.time.Instant;
import java.util.Objects;

/** Domain event: EasementClosed. */
public final class EasementClosed {
    private final EasementId id;
    private final Instant occurredAt;

    public EasementClosed(EasementId id, Instant occurredAt) {
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

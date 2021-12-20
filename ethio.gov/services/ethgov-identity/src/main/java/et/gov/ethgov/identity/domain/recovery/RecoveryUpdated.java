package et.gov.ethgov.identity.domain.recovery;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RecoveryUpdated. */
public final class RecoveryUpdated {
    private final RecoveryId id;
    private final Instant occurredAt;

    public RecoveryUpdated(RecoveryId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public RecoveryId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

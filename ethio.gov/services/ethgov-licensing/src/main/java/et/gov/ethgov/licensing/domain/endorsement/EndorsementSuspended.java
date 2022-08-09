package et.gov.ethgov.licensing.domain.endorsement;

import java.time.Instant;
import java.util.Objects;

/** Domain event: EndorsementSuspended. */
public final class EndorsementSuspended {
    private final EndorsementId id;
    private final Instant occurredAt;

    public EndorsementSuspended(EndorsementId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public EndorsementId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

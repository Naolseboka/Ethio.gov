package et.gov.ethgov.licensing.domain.endorsement;

import java.time.Instant;
import java.util.Objects;

/** Domain event: EndorsementUpdated. */
public final class EndorsementUpdated {
    private final EndorsementId id;
    private final Instant occurredAt;

    public EndorsementUpdated(EndorsementId id, Instant occurredAt) {
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

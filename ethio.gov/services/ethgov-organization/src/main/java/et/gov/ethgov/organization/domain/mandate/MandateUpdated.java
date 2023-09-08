package et.gov.ethgov.organization.domain.mandate;

import java.time.Instant;
import java.util.Objects;

/** Domain event: MandateUpdated. */
public final class MandateUpdated {
    private final MandateId id;
    private final Instant occurredAt;

    public MandateUpdated(MandateId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public MandateId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

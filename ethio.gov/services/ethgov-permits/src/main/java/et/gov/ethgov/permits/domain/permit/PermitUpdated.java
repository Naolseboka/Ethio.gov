package et.gov.ethgov.permits.domain.permit;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PermitUpdated. */
public final class PermitUpdated {
    private final PermitId id;
    private final Instant occurredAt;

    public PermitUpdated(PermitId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PermitId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.permits.domain.permit;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PermitSuspended. */
public final class PermitSuspended {
    private final PermitId id;
    private final Instant occurredAt;

    public PermitSuspended(PermitId id, Instant occurredAt) {
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

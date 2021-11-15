package et.gov.ethgov.identity.domain.delegation;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DelegationSuspended. */
public final class DelegationSuspended {
    private final DelegationId id;
    private final Instant occurredAt;

    public DelegationSuspended(DelegationId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DelegationId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

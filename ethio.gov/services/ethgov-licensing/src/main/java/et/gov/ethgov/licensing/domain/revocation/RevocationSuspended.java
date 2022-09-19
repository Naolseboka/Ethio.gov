package et.gov.ethgov.licensing.domain.revocation;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RevocationSuspended. */
public final class RevocationSuspended {
    private final RevocationId id;
    private final Instant occurredAt;

    public RevocationSuspended(RevocationId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public RevocationId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

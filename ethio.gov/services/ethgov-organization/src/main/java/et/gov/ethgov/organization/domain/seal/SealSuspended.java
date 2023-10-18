package et.gov.ethgov.organization.domain.seal;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SealSuspended. */
public final class SealSuspended {
    private final SealId id;
    private final Instant occurredAt;

    public SealSuspended(SealId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SealId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

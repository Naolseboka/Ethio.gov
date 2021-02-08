package et.gov.ethgov.gateway.domain.canary;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CanarySuspended. */
public final class CanarySuspended {
    private final CanaryId id;
    private final Instant occurredAt;

    public CanarySuspended(CanaryId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CanaryId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

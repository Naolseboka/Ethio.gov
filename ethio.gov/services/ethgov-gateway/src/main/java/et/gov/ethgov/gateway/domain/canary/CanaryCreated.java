package et.gov.ethgov.gateway.domain.canary;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CanaryCreated. */
public final class CanaryCreated {
    private final CanaryId id;
    private final Instant occurredAt;

    public CanaryCreated(CanaryId id, Instant occurredAt) {
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

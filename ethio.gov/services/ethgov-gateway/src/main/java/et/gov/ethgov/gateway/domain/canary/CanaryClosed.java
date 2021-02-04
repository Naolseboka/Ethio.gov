package et.gov.ethgov.gateway.domain.canary;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CanaryClosed. */
public final class CanaryClosed {
    private final CanaryId id;
    private final Instant occurredAt;

    public CanaryClosed(CanaryId id, Instant occurredAt) {
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

package et.gov.ethgov.gateway.domain.healthcheck;

import java.time.Instant;
import java.util.Objects;

/** Domain event: HealthcheckSuspended. */
public final class HealthcheckSuspended {
    private final HealthcheckId id;
    private final Instant occurredAt;

    public HealthcheckSuspended(HealthcheckId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public HealthcheckId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

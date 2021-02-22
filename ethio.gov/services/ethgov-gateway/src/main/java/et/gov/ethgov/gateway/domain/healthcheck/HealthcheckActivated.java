package et.gov.ethgov.gateway.domain.healthcheck;

import java.time.Instant;
import java.util.Objects;

/** Domain event: HealthcheckActivated. */
public final class HealthcheckActivated {
    private final HealthcheckId id;
    private final Instant occurredAt;

    public HealthcheckActivated(HealthcheckId id, Instant occurredAt) {
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

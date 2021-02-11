package et.gov.ethgov.gateway.domain.circuitbreaker;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CircuitbreakerSuspended. */
public final class CircuitbreakerSuspended {
    private final CircuitbreakerId id;
    private final Instant occurredAt;

    public CircuitbreakerSuspended(CircuitbreakerId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CircuitbreakerId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

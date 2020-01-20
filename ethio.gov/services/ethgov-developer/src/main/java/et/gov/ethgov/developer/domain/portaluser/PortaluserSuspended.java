package et.gov.ethgov.developer.domain.portaluser;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PortaluserSuspended. */
public final class PortaluserSuspended {
    private final PortaluserId id;
    private final Instant occurredAt;

    public PortaluserSuspended(PortaluserId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PortaluserId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

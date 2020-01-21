package et.gov.ethgov.developer.domain.portaluser;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PortaluserUpdated. */
public final class PortaluserUpdated {
    private final PortaluserId id;
    private final Instant occurredAt;

    public PortaluserUpdated(PortaluserId id, Instant occurredAt) {
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

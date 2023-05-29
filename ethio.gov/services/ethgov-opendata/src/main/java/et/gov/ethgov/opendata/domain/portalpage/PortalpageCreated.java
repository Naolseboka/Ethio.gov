package et.gov.ethgov.opendata.domain.portalpage;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PortalpageCreated. */
public final class PortalpageCreated {
    private final PortalpageId id;
    private final Instant occurredAt;

    public PortalpageCreated(PortalpageId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PortalpageId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

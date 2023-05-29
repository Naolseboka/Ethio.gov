package et.gov.ethgov.opendata.domain.portalpage;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PortalpageSuspended. */
public final class PortalpageSuspended {
    private final PortalpageId id;
    private final Instant occurredAt;

    public PortalpageSuspended(PortalpageId id, Instant occurredAt) {
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

package et.gov.ethgov.opendata.domain.portalpage;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PortalpageClosed. */
public final class PortalpageClosed {
    private final PortalpageId id;
    private final Instant occurredAt;

    public PortalpageClosed(PortalpageId id, Instant occurredAt) {
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

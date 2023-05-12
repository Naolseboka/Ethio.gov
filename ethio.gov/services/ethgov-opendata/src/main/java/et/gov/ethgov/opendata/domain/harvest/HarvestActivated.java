package et.gov.ethgov.opendata.domain.harvest;

import java.time.Instant;
import java.util.Objects;

/** Domain event: HarvestActivated. */
public final class HarvestActivated {
    private final HarvestId id;
    private final Instant occurredAt;

    public HarvestActivated(HarvestId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public HarvestId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.opendata.domain.harvest;

import java.time.Instant;
import java.util.Objects;

/** Domain event: HarvestUpdated. */
public final class HarvestUpdated {
    private final HarvestId id;
    private final Instant occurredAt;

    public HarvestUpdated(HarvestId id, Instant occurredAt) {
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

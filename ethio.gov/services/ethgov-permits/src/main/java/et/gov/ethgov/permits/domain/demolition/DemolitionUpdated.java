package et.gov.ethgov.permits.domain.demolition;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DemolitionUpdated. */
public final class DemolitionUpdated {
    private final DemolitionId id;
    private final Instant occurredAt;

    public DemolitionUpdated(DemolitionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DemolitionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

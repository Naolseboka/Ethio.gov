package et.gov.ethgov.permits.domain.demolition;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DemolitionActivated. */
public final class DemolitionActivated {
    private final DemolitionId id;
    private final Instant occurredAt;

    public DemolitionActivated(DemolitionId id, Instant occurredAt) {
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

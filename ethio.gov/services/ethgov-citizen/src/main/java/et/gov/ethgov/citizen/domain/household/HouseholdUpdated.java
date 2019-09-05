package et.gov.ethgov.citizen.domain.household;

import java.time.Instant;
import java.util.Objects;

/** Domain event: HouseholdUpdated. */
public final class HouseholdUpdated {
    private final HouseholdId id;
    private final Instant occurredAt;

    public HouseholdUpdated(HouseholdId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public HouseholdId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.citizen.domain.household;

import java.time.Instant;
import java.util.Objects;

/** Domain event: HouseholdActivated. */
public final class HouseholdActivated {
    private final HouseholdId id;
    private final Instant occurredAt;

    public HouseholdActivated(HouseholdId id, Instant occurredAt) {
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

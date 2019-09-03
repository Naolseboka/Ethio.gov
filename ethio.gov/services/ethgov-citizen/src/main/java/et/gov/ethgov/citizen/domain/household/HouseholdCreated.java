package et.gov.ethgov.citizen.domain.household;

import java.time.Instant;
import java.util.Objects;

/** Domain event: HouseholdCreated. */
public final class HouseholdCreated {
    private final HouseholdId id;
    private final Instant occurredAt;

    public HouseholdCreated(HouseholdId id, Instant occurredAt) {
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

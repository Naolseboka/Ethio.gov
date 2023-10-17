package et.gov.ethgov.organization.domain.rolebinding;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RolebindingUpdated. */
public final class RolebindingUpdated {
    private final RolebindingId id;
    private final Instant occurredAt;

    public RolebindingUpdated(RolebindingId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public RolebindingId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

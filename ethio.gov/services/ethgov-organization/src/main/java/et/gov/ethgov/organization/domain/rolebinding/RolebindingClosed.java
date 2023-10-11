package et.gov.ethgov.organization.domain.rolebinding;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RolebindingClosed. */
public final class RolebindingClosed {
    private final RolebindingId id;
    private final Instant occurredAt;

    public RolebindingClosed(RolebindingId id, Instant occurredAt) {
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

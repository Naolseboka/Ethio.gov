package et.gov.ethgov.licensing.domain.inspection;

import java.time.Instant;
import java.util.Objects;

/** Domain event: InspectionSuspended. */
public final class InspectionSuspended {
    private final InspectionId id;
    private final Instant occurredAt;

    public InspectionSuspended(InspectionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public InspectionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

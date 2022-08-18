package et.gov.ethgov.licensing.domain.inspection;

import java.time.Instant;
import java.util.Objects;

/** Domain event: InspectionActivated. */
public final class InspectionActivated {
    private final InspectionId id;
    private final Instant occurredAt;

    public InspectionActivated(InspectionId id, Instant occurredAt) {
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

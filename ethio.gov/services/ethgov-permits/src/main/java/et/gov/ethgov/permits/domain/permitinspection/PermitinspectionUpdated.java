package et.gov.ethgov.permits.domain.permitinspection;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PermitinspectionUpdated. */
public final class PermitinspectionUpdated {
    private final PermitinspectionId id;
    private final Instant occurredAt;

    public PermitinspectionUpdated(PermitinspectionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PermitinspectionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

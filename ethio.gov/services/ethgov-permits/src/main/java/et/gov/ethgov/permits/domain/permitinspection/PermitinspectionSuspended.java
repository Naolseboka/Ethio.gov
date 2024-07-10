package et.gov.ethgov.permits.domain.permitinspection;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PermitinspectionSuspended. */
public final class PermitinspectionSuspended {
    private final PermitinspectionId id;
    private final Instant occurredAt;

    public PermitinspectionSuspended(PermitinspectionId id, Instant occurredAt) {
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

package et.gov.ethgov.permits.domain.permitinspection;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PermitinspectionClosed. */
public final class PermitinspectionClosed {
    private final PermitinspectionId id;
    private final Instant occurredAt;

    public PermitinspectionClosed(PermitinspectionId id, Instant occurredAt) {
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

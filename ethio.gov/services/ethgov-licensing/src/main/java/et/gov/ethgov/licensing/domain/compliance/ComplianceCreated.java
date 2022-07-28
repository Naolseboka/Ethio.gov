package et.gov.ethgov.licensing.domain.compliance;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ComplianceCreated. */
public final class ComplianceCreated {
    private final ComplianceId id;
    private final Instant occurredAt;

    public ComplianceCreated(ComplianceId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ComplianceId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

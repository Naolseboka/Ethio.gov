package et.gov.ethgov.licensing.domain.compliance;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ComplianceActivated. */
public final class ComplianceActivated {
    private final ComplianceId id;
    private final Instant occurredAt;

    public ComplianceActivated(ComplianceId id, Instant occurredAt) {
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

package et.gov.ethgov.licensing.domain.compliance;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ComplianceSuspended. */
public final class ComplianceSuspended {
    private final ComplianceId id;
    private final Instant occurredAt;

    public ComplianceSuspended(ComplianceId id, Instant occurredAt) {
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

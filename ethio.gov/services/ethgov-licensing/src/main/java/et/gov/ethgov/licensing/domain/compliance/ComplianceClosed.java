package et.gov.ethgov.licensing.domain.compliance;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ComplianceClosed. */
public final class ComplianceClosed {
    private final ComplianceId id;
    private final Instant occurredAt;

    public ComplianceClosed(ComplianceId id, Instant occurredAt) {
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

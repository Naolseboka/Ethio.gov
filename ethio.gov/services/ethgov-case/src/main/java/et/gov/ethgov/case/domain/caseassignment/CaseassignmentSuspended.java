package et.gov.ethgov.case.domain.caseassignment;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CaseassignmentSuspended. */
public final class CaseassignmentSuspended {
    private final CaseassignmentId id;
    private final Instant occurredAt;

    public CaseassignmentSuspended(CaseassignmentId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CaseassignmentId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

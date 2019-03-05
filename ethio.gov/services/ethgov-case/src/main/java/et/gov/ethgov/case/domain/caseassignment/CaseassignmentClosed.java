package et.gov.ethgov.case.domain.caseassignment;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CaseassignmentClosed. */
public final class CaseassignmentClosed {
    private final CaseassignmentId id;
    private final Instant occurredAt;

    public CaseassignmentClosed(CaseassignmentId id, Instant occurredAt) {
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

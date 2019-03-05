package et.gov.ethgov.case.domain.caseassignment;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CaseassignmentCreated. */
public final class CaseassignmentCreated {
    private final CaseassignmentId id;
    private final Instant occurredAt;

    public CaseassignmentCreated(CaseassignmentId id, Instant occurredAt) {
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

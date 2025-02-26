package et.gov.ethgov.revenue.domain.assessment;

import java.time.Instant;
import java.util.Objects;

/** Domain event: AssessmentCreated. */
public final class AssessmentCreated {
    private final AssessmentId id;
    private final Instant occurredAt;

    public AssessmentCreated(AssessmentId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public AssessmentId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

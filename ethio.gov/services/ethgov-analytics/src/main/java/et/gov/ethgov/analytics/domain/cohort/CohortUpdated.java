package et.gov.ethgov.analytics.domain.cohort;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CohortUpdated. */
public final class CohortUpdated {
    private final CohortId id;
    private final Instant occurredAt;

    public CohortUpdated(CohortId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CohortId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.analytics.domain.cohort;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CohortClosed. */
public final class CohortClosed {
    private final CohortId id;
    private final Instant occurredAt;

    public CohortClosed(CohortId id, Instant occurredAt) {
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

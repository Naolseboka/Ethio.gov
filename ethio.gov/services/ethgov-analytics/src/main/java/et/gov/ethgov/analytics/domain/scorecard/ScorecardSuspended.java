package et.gov.ethgov.analytics.domain.scorecard;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ScorecardSuspended. */
public final class ScorecardSuspended {
    private final ScorecardId id;
    private final Instant occurredAt;

    public ScorecardSuspended(ScorecardId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ScorecardId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

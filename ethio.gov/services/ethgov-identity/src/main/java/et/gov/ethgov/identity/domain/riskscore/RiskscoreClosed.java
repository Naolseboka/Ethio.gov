package et.gov.ethgov.identity.domain.riskscore;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RiskscoreClosed. */
public final class RiskscoreClosed {
    private final RiskscoreId id;
    private final Instant occurredAt;

    public RiskscoreClosed(RiskscoreId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public RiskscoreId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

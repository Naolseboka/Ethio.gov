package et.gov.ethgov.search.domain.ranking;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RankingCreated. */
public final class RankingCreated {
    private final RankingId id;
    private final Instant occurredAt;

    public RankingCreated(RankingId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public RankingId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

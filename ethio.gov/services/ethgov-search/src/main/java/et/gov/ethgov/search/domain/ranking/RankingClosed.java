package et.gov.ethgov.search.domain.ranking;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RankingClosed. */
public final class RankingClosed {
    private final RankingId id;
    private final Instant occurredAt;

    public RankingClosed(RankingId id, Instant occurredAt) {
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

package et.gov.ethgov.search.domain.boost;

import java.time.Instant;
import java.util.Objects;

/** Domain event: BoostSuspended. */
public final class BoostSuspended {
    private final BoostId id;
    private final Instant occurredAt;

    public BoostSuspended(BoostId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public BoostId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

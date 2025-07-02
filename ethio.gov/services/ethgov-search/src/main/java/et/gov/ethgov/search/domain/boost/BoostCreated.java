package et.gov.ethgov.search.domain.boost;

import java.time.Instant;
import java.util.Objects;

/** Domain event: BoostCreated. */
public final class BoostCreated {
    private final BoostId id;
    private final Instant occurredAt;

    public BoostCreated(BoostId id, Instant occurredAt) {
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

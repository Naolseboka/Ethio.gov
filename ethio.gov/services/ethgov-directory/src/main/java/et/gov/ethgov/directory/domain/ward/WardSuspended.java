package et.gov.ethgov.directory.domain.ward;

import java.time.Instant;
import java.util.Objects;

/** Domain event: WardSuspended. */
public final class WardSuspended {
    private final WardId id;
    private final Instant occurredAt;

    public WardSuspended(WardId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public WardId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

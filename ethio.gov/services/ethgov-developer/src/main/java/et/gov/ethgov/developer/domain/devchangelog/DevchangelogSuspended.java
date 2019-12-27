package et.gov.ethgov.developer.domain.devchangelog;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DevchangelogSuspended. */
public final class DevchangelogSuspended {
    private final DevchangelogId id;
    private final Instant occurredAt;

    public DevchangelogSuspended(DevchangelogId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DevchangelogId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

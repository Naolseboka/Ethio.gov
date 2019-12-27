package et.gov.ethgov.developer.domain.devchangelog;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DevchangelogCreated. */
public final class DevchangelogCreated {
    private final DevchangelogId id;
    private final Instant occurredAt;

    public DevchangelogCreated(DevchangelogId id, Instant occurredAt) {
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

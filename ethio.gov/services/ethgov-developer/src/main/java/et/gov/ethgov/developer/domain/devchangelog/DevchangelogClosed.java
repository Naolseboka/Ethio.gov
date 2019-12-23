package et.gov.ethgov.developer.domain.devchangelog;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DevchangelogClosed. */
public final class DevchangelogClosed {
    private final DevchangelogId id;
    private final Instant occurredAt;

    public DevchangelogClosed(DevchangelogId id, Instant occurredAt) {
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

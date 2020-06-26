package et.gov.ethgov.directory.domain.ward;

import java.time.Instant;
import java.util.Objects;

/** Domain event: WardClosed. */
public final class WardClosed {
    private final WardId id;
    private final Instant occurredAt;

    public WardClosed(WardId id, Instant occurredAt) {
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

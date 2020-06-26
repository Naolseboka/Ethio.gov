package et.gov.ethgov.directory.domain.ward;

import java.time.Instant;
import java.util.Objects;

/** Domain event: WardCreated. */
public final class WardCreated {
    private final WardId id;
    private final Instant occurredAt;

    public WardCreated(WardId id, Instant occurredAt) {
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

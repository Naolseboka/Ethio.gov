package et.gov.ethgov.directory.domain.ward;

import java.time.Instant;
import java.util.Objects;

/** Domain event: WardActivated. */
public final class WardActivated {
    private final WardId id;
    private final Instant occurredAt;

    public WardActivated(WardId id, Instant occurredAt) {
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

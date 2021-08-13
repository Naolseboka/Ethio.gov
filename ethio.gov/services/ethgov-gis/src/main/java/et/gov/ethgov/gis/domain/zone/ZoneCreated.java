package et.gov.ethgov.gis.domain.zone;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ZoneCreated. */
public final class ZoneCreated {
    private final ZoneId id;
    private final Instant occurredAt;

    public ZoneCreated(ZoneId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ZoneId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

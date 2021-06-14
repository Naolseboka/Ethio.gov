package et.gov.ethgov.gis.domain.basemap;

import java.time.Instant;
import java.util.Objects;

/** Domain event: BasemapSuspended. */
public final class BasemapSuspended {
    private final BasemapId id;
    private final Instant occurredAt;

    public BasemapSuspended(BasemapId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public BasemapId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.gis.domain.basemap;

import java.time.Instant;
import java.util.Objects;

/** Domain event: BasemapActivated. */
public final class BasemapActivated {
    private final BasemapId id;
    private final Instant occurredAt;

    public BasemapActivated(BasemapId id, Instant occurredAt) {
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

package et.gov.ethgov.gis.domain.geocode;

import java.time.Instant;
import java.util.Objects;

/** Domain event: GeocodeClosed. */
public final class GeocodeClosed {
    private final GeocodeId id;
    private final Instant occurredAt;

    public GeocodeClosed(GeocodeId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public GeocodeId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

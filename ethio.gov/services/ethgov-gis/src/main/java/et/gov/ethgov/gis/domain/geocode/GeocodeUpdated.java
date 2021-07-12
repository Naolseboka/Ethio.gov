package et.gov.ethgov.gis.domain.geocode;

import java.time.Instant;
import java.util.Objects;

/** Domain event: GeocodeUpdated. */
public final class GeocodeUpdated {
    private final GeocodeId id;
    private final Instant occurredAt;

    public GeocodeUpdated(GeocodeId id, Instant occurredAt) {
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

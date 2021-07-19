package et.gov.ethgov.gis.domain.gisexport;

import java.time.Instant;
import java.util.Objects;

/** Domain event: GisexportUpdated. */
public final class GisexportUpdated {
    private final GisexportId id;
    private final Instant occurredAt;

    public GisexportUpdated(GisexportId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public GisexportId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

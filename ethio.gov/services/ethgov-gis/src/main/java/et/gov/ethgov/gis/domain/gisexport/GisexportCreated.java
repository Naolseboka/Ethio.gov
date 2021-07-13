package et.gov.ethgov.gis.domain.gisexport;

import java.time.Instant;
import java.util.Objects;

/** Domain event: GisexportCreated. */
public final class GisexportCreated {
    private final GisexportId id;
    private final Instant occurredAt;

    public GisexportCreated(GisexportId id, Instant occurredAt) {
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

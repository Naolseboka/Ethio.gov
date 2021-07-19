package et.gov.ethgov.gis.domain.heatmap;

import java.time.Instant;
import java.util.Objects;

/** Domain event: HeatmapCreated. */
public final class HeatmapCreated {
    private final HeatmapId id;
    private final Instant occurredAt;

    public HeatmapCreated(HeatmapId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public HeatmapId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.gis.domain.heatmap;

import java.time.Instant;
import java.util.Objects;

/** Domain event: HeatmapUpdated. */
public final class HeatmapUpdated {
    private final HeatmapId id;
    private final Instant occurredAt;

    public HeatmapUpdated(HeatmapId id, Instant occurredAt) {
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

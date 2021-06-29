package et.gov.ethgov.gis.domain.feature;

import java.time.Instant;
import java.util.Objects;

/** Domain event: FeatureClosed. */
public final class FeatureClosed {
    private final FeatureId id;
    private final Instant occurredAt;

    public FeatureClosed(FeatureId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public FeatureId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

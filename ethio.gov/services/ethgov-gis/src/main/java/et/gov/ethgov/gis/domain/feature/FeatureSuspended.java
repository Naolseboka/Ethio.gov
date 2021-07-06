package et.gov.ethgov.gis.domain.feature;

import java.time.Instant;
import java.util.Objects;

/** Domain event: FeatureSuspended. */
public final class FeatureSuspended {
    private final FeatureId id;
    private final Instant occurredAt;

    public FeatureSuspended(FeatureId id, Instant occurredAt) {
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

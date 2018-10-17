package et.gov.ethgov.analytics.domain.anomaly;

import java.time.Instant;
import java.util.Objects;

/** Domain event: AnomalyCreated. */
public final class AnomalyCreated {
    private final AnomalyId id;
    private final Instant occurredAt;

    public AnomalyCreated(AnomalyId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public AnomalyId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

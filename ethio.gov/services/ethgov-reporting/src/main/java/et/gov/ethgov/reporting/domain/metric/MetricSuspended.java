package et.gov.ethgov.reporting.domain.metric;

import java.time.Instant;
import java.util.Objects;

/** Domain event: MetricSuspended. */
public final class MetricSuspended {
    private final MetricId id;
    private final Instant occurredAt;

    public MetricSuspended(MetricId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public MetricId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

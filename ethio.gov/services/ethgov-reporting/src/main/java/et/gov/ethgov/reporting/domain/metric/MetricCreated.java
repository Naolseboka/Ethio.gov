package et.gov.ethgov.reporting.domain.metric;

import java.time.Instant;
import java.util.Objects;

/** Domain event: MetricCreated. */
public final class MetricCreated {
    private final MetricId id;
    private final Instant occurredAt;

    public MetricCreated(MetricId id, Instant occurredAt) {
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

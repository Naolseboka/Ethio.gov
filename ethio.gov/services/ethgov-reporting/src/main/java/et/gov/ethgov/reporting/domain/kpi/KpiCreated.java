package et.gov.ethgov.reporting.domain.kpi;

import java.time.Instant;
import java.util.Objects;

/** Domain event: KpiCreated. */
public final class KpiCreated {
    private final KpiId id;
    private final Instant occurredAt;

    public KpiCreated(KpiId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public KpiId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

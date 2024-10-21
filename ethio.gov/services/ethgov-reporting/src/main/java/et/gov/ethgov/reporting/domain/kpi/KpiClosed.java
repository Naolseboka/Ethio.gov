package et.gov.ethgov.reporting.domain.kpi;

import java.time.Instant;
import java.util.Objects;

/** Domain event: KpiClosed. */
public final class KpiClosed {
    private final KpiId id;
    private final Instant occurredAt;

    public KpiClosed(KpiId id, Instant occurredAt) {
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

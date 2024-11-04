package et.gov.ethgov.reporting.domain.reportalert;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ReportalertClosed. */
public final class ReportalertClosed {
    private final ReportalertId id;
    private final Instant occurredAt;

    public ReportalertClosed(ReportalertId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ReportalertId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

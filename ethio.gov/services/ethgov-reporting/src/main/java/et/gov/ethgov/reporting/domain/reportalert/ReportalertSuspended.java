package et.gov.ethgov.reporting.domain.reportalert;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ReportalertSuspended. */
public final class ReportalertSuspended {
    private final ReportalertId id;
    private final Instant occurredAt;

    public ReportalertSuspended(ReportalertId id, Instant occurredAt) {
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

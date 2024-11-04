package et.gov.ethgov.reporting.domain.reportalert;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ReportalertActivated. */
public final class ReportalertActivated {
    private final ReportalertId id;
    private final Instant occurredAt;

    public ReportalertActivated(ReportalertId id, Instant occurredAt) {
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

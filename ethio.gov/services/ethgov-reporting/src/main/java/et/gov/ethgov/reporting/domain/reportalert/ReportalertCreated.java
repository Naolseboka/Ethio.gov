package et.gov.ethgov.reporting.domain.reportalert;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ReportalertCreated. */
public final class ReportalertCreated {
    private final ReportalertId id;
    private final Instant occurredAt;

    public ReportalertCreated(ReportalertId id, Instant occurredAt) {
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

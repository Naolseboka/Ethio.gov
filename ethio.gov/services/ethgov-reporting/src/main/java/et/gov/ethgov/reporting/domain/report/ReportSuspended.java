package et.gov.ethgov.reporting.domain.report;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ReportSuspended. */
public final class ReportSuspended {
    private final ReportId id;
    private final Instant occurredAt;

    public ReportSuspended(ReportId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ReportId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

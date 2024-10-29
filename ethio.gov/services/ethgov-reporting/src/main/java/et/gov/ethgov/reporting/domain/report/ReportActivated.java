package et.gov.ethgov.reporting.domain.report;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ReportActivated. */
public final class ReportActivated {
    private final ReportId id;
    private final Instant occurredAt;

    public ReportActivated(ReportId id, Instant occurredAt) {
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

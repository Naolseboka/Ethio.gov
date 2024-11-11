package et.gov.ethgov.reporting.domain.reportexport;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ReportexportClosed. */
public final class ReportexportClosed {
    private final ReportexportId id;
    private final Instant occurredAt;

    public ReportexportClosed(ReportexportId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ReportexportId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.reporting.domain.reportfilter;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ReportfilterCreated. */
public final class ReportfilterCreated {
    private final ReportfilterId id;
    private final Instant occurredAt;

    public ReportfilterCreated(ReportfilterId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ReportfilterId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

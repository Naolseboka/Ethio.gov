package et.gov.ethgov.reporting.domain.reportsubscription;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ReportsubscriptionUpdated. */
public final class ReportsubscriptionUpdated {
    private final ReportsubscriptionId id;
    private final Instant occurredAt;

    public ReportsubscriptionUpdated(ReportsubscriptionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ReportsubscriptionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.reporting.domain.reportsubscription;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ReportsubscriptionActivated. */
public final class ReportsubscriptionActivated {
    private final ReportsubscriptionId id;
    private final Instant occurredAt;

    public ReportsubscriptionActivated(ReportsubscriptionId id, Instant occurredAt) {
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

package et.gov.ethgov.revenue.domain.exemption;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ExemptionUpdated. */
public final class ExemptionUpdated {
    private final ExemptionId id;
    private final Instant occurredAt;

    public ExemptionUpdated(ExemptionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ExemptionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

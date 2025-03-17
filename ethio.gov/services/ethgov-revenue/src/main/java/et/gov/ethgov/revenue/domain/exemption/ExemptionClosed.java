package et.gov.ethgov.revenue.domain.exemption;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ExemptionClosed. */
public final class ExemptionClosed {
    private final ExemptionId id;
    private final Instant occurredAt;

    public ExemptionClosed(ExemptionId id, Instant occurredAt) {
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

package et.gov.ethgov.revenue.domain.installment;

import java.time.Instant;
import java.util.Objects;

/** Domain event: InstallmentSuspended. */
public final class InstallmentSuspended {
    private final InstallmentId id;
    private final Instant occurredAt;

    public InstallmentSuspended(InstallmentId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public InstallmentId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.revenue.domain.installment;

import java.time.Instant;
import java.util.Objects;

/** Domain event: InstallmentCreated. */
public final class InstallmentCreated {
    private final InstallmentId id;
    private final Instant occurredAt;

    public InstallmentCreated(InstallmentId id, Instant occurredAt) {
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

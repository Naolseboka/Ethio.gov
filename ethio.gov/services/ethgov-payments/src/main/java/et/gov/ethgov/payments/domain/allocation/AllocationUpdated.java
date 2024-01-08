package et.gov.ethgov.payments.domain.allocation;

import java.time.Instant;
import java.util.Objects;

/** Domain event: AllocationUpdated. */
public final class AllocationUpdated {
    private final AllocationId id;
    private final Instant occurredAt;

    public AllocationUpdated(AllocationId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public AllocationId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

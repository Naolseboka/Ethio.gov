package et.gov.ethgov.analytics.domain.warehouse;

import java.time.Instant;
import java.util.Objects;

/** Domain event: WarehouseCreated. */
public final class WarehouseCreated {
    private final WarehouseId id;
    private final Instant occurredAt;

    public WarehouseCreated(WarehouseId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public WarehouseId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

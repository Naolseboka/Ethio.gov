package et.gov.ethgov.analytics.domain.warehouse;

import java.time.Instant;
import java.util.Objects;

/** Domain event: WarehouseSuspended. */
public final class WarehouseSuspended {
    private final WarehouseId id;
    private final Instant occurredAt;

    public WarehouseSuspended(WarehouseId id, Instant occurredAt) {
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

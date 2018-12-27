package et.gov.ethgov.analytics.domain.warehouse;

import java.time.Instant;
import java.util.Objects;

/** Domain event: WarehouseUpdated. */
public final class WarehouseUpdated {
    private final WarehouseId id;
    private final Instant occurredAt;

    public WarehouseUpdated(WarehouseId id, Instant occurredAt) {
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

package et.gov.ethgov.analytics.domain.warehouse;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Warehouse. */
public final class WarehouseId {
    private final UUID value;

    private WarehouseId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static WarehouseId newId() {
        return new WarehouseId(UUID.randomUUID());
    }

    public static WarehouseId of(UUID value) {
        return new WarehouseId(value);
    }

    public static WarehouseId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new WarehouseId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WarehouseId other)) {
            return false;
        }
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

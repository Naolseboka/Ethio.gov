package et.gov.ethgov.notification.domain.delivery;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Delivery. */
public final class DeliveryId {
    private final UUID value;

    private DeliveryId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DeliveryId newId() {
        return new DeliveryId(UUID.randomUUID());
    }

    public static DeliveryId of(UUID value) {
        return new DeliveryId(value);
    }

    public static DeliveryId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DeliveryId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeliveryId other)) {
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

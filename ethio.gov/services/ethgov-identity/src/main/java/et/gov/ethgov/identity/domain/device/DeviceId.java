package et.gov.ethgov.identity.domain.device;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Device. */
public final class DeviceId {
    private final UUID value;

    private DeviceId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DeviceId newId() {
        return new DeviceId(UUID.randomUUID());
    }

    public static DeviceId of(UUID value) {
        return new DeviceId(value);
    }

    public static DeviceId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DeviceId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeviceId other)) {
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

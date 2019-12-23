package et.gov.ethgov.developer.domain.devapplication;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Devapplication. */
public final class DevapplicationId {
    private final UUID value;

    private DevapplicationId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DevapplicationId newId() {
        return new DevapplicationId(UUID.randomUUID());
    }

    public static DevapplicationId of(UUID value) {
        return new DevapplicationId(value);
    }

    public static DevapplicationId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DevapplicationId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DevapplicationId other)) {
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

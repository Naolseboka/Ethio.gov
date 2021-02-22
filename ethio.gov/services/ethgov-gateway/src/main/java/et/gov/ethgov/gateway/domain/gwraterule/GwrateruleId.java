package et.gov.ethgov.gateway.domain.gwraterule;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Gwraterule. */
public final class GwrateruleId {
    private final UUID value;

    private GwrateruleId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static GwrateruleId newId() {
        return new GwrateruleId(UUID.randomUUID());
    }

    public static GwrateruleId of(UUID value) {
        return new GwrateruleId(value);
    }

    public static GwrateruleId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new GwrateruleId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GwrateruleId other)) {
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

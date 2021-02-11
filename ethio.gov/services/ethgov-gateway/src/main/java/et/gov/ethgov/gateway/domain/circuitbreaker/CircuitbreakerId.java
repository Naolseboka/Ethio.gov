package et.gov.ethgov.gateway.domain.circuitbreaker;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Circuitbreaker. */
public final class CircuitbreakerId {
    private final UUID value;

    private CircuitbreakerId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CircuitbreakerId newId() {
        return new CircuitbreakerId(UUID.randomUUID());
    }

    public static CircuitbreakerId of(UUID value) {
        return new CircuitbreakerId(value);
    }

    public static CircuitbreakerId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CircuitbreakerId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CircuitbreakerId other)) {
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

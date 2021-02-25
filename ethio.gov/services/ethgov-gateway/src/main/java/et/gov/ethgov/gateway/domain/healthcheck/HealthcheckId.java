package et.gov.ethgov.gateway.domain.healthcheck;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Healthcheck. */
public final class HealthcheckId {
    private final UUID value;

    private HealthcheckId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static HealthcheckId newId() {
        return new HealthcheckId(UUID.randomUUID());
    }

    public static HealthcheckId of(UUID value) {
        return new HealthcheckId(value);
    }

    public static HealthcheckId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new HealthcheckId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HealthcheckId other)) {
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

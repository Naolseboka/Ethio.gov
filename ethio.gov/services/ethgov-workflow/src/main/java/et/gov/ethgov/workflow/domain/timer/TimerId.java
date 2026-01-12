package et.gov.ethgov.workflow.domain.timer;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Timer. */
public final class TimerId {
    private final UUID value;

    private TimerId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static TimerId newId() {
        return new TimerId(UUID.randomUUID());
    }

    public static TimerId of(UUID value) {
        return new TimerId(value);
    }

    public static TimerId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new TimerId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimerId other)) {
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

package et.gov.ethgov.gateway.domain.canary;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Canary. */
public final class CanaryId {
    private final UUID value;

    private CanaryId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CanaryId newId() {
        return new CanaryId(UUID.randomUUID());
    }

    public static CanaryId of(UUID value) {
        return new CanaryId(value);
    }

    public static CanaryId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CanaryId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CanaryId other)) {
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

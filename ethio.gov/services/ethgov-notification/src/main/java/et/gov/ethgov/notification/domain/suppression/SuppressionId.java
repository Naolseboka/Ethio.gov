package et.gov.ethgov.notification.domain.suppression;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Suppression. */
public final class SuppressionId {
    private final UUID value;

    private SuppressionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SuppressionId newId() {
        return new SuppressionId(UUID.randomUUID());
    }

    public static SuppressionId of(UUID value) {
        return new SuppressionId(value);
    }

    public static SuppressionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SuppressionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SuppressionId other)) {
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

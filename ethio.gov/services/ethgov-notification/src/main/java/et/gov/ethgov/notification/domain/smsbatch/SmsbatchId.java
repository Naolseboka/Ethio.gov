package et.gov.ethgov.notification.domain.smsbatch;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Smsbatch. */
public final class SmsbatchId {
    private final UUID value;

    private SmsbatchId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SmsbatchId newId() {
        return new SmsbatchId(UUID.randomUUID());
    }

    public static SmsbatchId of(UUID value) {
        return new SmsbatchId(value);
    }

    public static SmsbatchId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SmsbatchId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SmsbatchId other)) {
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

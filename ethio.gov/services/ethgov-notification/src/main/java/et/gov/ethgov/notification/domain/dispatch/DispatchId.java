package et.gov.ethgov.notification.domain.dispatch;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Dispatch. */
public final class DispatchId {
    private final UUID value;

    private DispatchId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DispatchId newId() {
        return new DispatchId(UUID.randomUUID());
    }

    public static DispatchId of(UUID value) {
        return new DispatchId(value);
    }

    public static DispatchId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DispatchId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DispatchId other)) {
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

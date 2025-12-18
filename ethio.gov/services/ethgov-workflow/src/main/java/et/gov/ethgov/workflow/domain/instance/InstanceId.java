package et.gov.ethgov.workflow.domain.instance;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Instance. */
public final class InstanceId {
    private final UUID value;

    private InstanceId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static InstanceId newId() {
        return new InstanceId(UUID.randomUUID());
    }

    public static InstanceId of(UUID value) {
        return new InstanceId(value);
    }

    public static InstanceId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new InstanceId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstanceId other)) {
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

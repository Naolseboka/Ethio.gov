package et.gov.ethgov.developer.domain.usage;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Usage. */
public final class UsageId {
    private final UUID value;

    private UsageId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static UsageId newId() {
        return new UsageId(UUID.randomUUID());
    }

    public static UsageId of(UUID value) {
        return new UsageId(value);
    }

    public static UsageId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new UsageId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UsageId other)) {
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

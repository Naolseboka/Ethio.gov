package et.gov.ethgov.licensing.domain.suspension;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Suspension. */
public final class SuspensionId {
    private final UUID value;

    private SuspensionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SuspensionId newId() {
        return new SuspensionId(UUID.randomUUID());
    }

    public static SuspensionId of(UUID value) {
        return new SuspensionId(value);
    }

    public static SuspensionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SuspensionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SuspensionId other)) {
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

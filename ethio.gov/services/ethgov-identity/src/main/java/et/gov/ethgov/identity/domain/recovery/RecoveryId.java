package et.gov.ethgov.identity.domain.recovery;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Recovery. */
public final class RecoveryId {
    private final UUID value;

    private RecoveryId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static RecoveryId newId() {
        return new RecoveryId(UUID.randomUUID());
    }

    public static RecoveryId of(UUID value) {
        return new RecoveryId(value);
    }

    public static RecoveryId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new RecoveryId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecoveryId other)) {
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

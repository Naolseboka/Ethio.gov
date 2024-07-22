package et.gov.ethgov.permits.domain.temporary;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Temporary. */
public final class TemporaryId {
    private final UUID value;

    private TemporaryId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static TemporaryId newId() {
        return new TemporaryId(UUID.randomUUID());
    }

    public static TemporaryId of(UUID value) {
        return new TemporaryId(value);
    }

    public static TemporaryId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new TemporaryId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TemporaryId other)) {
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

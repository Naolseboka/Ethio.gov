package et.gov.ethgov.document.domain.retention;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Retention. */
public final class RetentionId {
    private final UUID value;

    private RetentionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static RetentionId newId() {
        return new RetentionId(UUID.randomUUID());
    }

    public static RetentionId of(UUID value) {
        return new RetentionId(value);
    }

    public static RetentionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new RetentionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RetentionId other)) {
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

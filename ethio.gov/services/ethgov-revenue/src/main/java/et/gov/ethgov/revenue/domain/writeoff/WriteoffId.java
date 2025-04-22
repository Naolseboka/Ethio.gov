package et.gov.ethgov.revenue.domain.writeoff;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Writeoff. */
public final class WriteoffId {
    private final UUID value;

    private WriteoffId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static WriteoffId newId() {
        return new WriteoffId(UUID.randomUUID());
    }

    public static WriteoffId of(UUID value) {
        return new WriteoffId(value);
    }

    public static WriteoffId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new WriteoffId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WriteoffId other)) {
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

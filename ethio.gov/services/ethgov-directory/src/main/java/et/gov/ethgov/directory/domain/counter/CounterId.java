package et.gov.ethgov.directory.domain.counter;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Counter. */
public final class CounterId {
    private final UUID value;

    private CounterId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CounterId newId() {
        return new CounterId(UUID.randomUUID());
    }

    public static CounterId of(UUID value) {
        return new CounterId(value);
    }

    public static CounterId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CounterId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CounterId other)) {
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

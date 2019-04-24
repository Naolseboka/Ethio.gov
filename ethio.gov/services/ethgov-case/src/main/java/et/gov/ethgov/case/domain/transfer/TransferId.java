package et.gov.ethgov.case.domain.transfer;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Transfer. */
public final class TransferId {
    private final UUID value;

    private TransferId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static TransferId newId() {
        return new TransferId(UUID.randomUUID());
    }

    public static TransferId of(UUID value) {
        return new TransferId(value);
    }

    public static TransferId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new TransferId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransferId other)) {
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

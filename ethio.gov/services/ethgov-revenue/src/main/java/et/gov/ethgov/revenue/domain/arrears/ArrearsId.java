package et.gov.ethgov.revenue.domain.arrears;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Arrears. */
public final class ArrearsId {
    private final UUID value;

    private ArrearsId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ArrearsId newId() {
        return new ArrearsId(UUID.randomUUID());
    }

    public static ArrearsId of(UUID value) {
        return new ArrearsId(value);
    }

    public static ArrearsId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ArrearsId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArrearsId other)) {
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

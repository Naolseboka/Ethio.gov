package et.gov.ethgov.payments.domain.refund;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Refund. */
public final class RefundId {
    private final UUID value;

    private RefundId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static RefundId newId() {
        return new RefundId(UUID.randomUUID());
    }

    public static RefundId of(UUID value) {
        return new RefundId(value);
    }

    public static RefundId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new RefundId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RefundId other)) {
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

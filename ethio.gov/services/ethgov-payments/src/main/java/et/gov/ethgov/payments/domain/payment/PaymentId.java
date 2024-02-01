package et.gov.ethgov.payments.domain.payment;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Payment. */
public final class PaymentId {
    private final UUID value;

    private PaymentId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PaymentId newId() {
        return new PaymentId(UUID.randomUUID());
    }

    public static PaymentId of(UUID value) {
        return new PaymentId(value);
    }

    public static PaymentId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PaymentId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentId other)) {
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

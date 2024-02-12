package et.gov.ethgov.payments.domain.paymentplan;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Paymentplan. */
public final class PaymentplanId {
    private final UUID value;

    private PaymentplanId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PaymentplanId newId() {
        return new PaymentplanId(UUID.randomUUID());
    }

    public static PaymentplanId of(UUID value) {
        return new PaymentplanId(value);
    }

    public static PaymentplanId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PaymentplanId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentplanId other)) {
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

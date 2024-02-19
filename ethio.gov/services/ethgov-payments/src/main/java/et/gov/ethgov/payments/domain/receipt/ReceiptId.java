package et.gov.ethgov.payments.domain.receipt;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Receipt. */
public final class ReceiptId {
    private final UUID value;

    private ReceiptId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ReceiptId newId() {
        return new ReceiptId(UUID.randomUUID());
    }

    public static ReceiptId of(UUID value) {
        return new ReceiptId(value);
    }

    public static ReceiptId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ReceiptId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReceiptId other)) {
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

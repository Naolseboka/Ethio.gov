package et.gov.ethgov.payments.domain.invoice;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Invoice. */
public final class InvoiceId {
    private final UUID value;

    private InvoiceId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static InvoiceId newId() {
        return new InvoiceId(UUID.randomUUID());
    }

    public static InvoiceId of(UUID value) {
        return new InvoiceId(value);
    }

    public static InvoiceId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new InvoiceId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvoiceId other)) {
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

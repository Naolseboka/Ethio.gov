package et.gov.ethgov.revenue.domain.taxbill;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Taxbill. */
public final class TaxbillId {
    private final UUID value;

    private TaxbillId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static TaxbillId newId() {
        return new TaxbillId(UUID.randomUUID());
    }

    public static TaxbillId of(UUID value) {
        return new TaxbillId(value);
    }

    public static TaxbillId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new TaxbillId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaxbillId other)) {
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

package et.gov.ethgov.land.domain.valuation;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Valuation. */
public final class ValuationId {
    private final UUID value;

    private ValuationId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ValuationId newId() {
        return new ValuationId(UUID.randomUUID());
    }

    public static ValuationId of(UUID value) {
        return new ValuationId(value);
    }

    public static ValuationId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ValuationId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ValuationId other)) {
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

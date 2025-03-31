package et.gov.ethgov.revenue.domain.ledger;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Ledger. */
public final class LedgerId {
    private final UUID value;

    private LedgerId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static LedgerId newId() {
        return new LedgerId(UUID.randomUUID());
    }

    public static LedgerId of(UUID value) {
        return new LedgerId(value);
    }

    public static LedgerId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new LedgerId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LedgerId other)) {
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

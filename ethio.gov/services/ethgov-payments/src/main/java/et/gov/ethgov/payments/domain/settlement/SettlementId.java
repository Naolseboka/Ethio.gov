package et.gov.ethgov.payments.domain.settlement;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Settlement. */
public final class SettlementId {
    private final UUID value;

    private SettlementId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SettlementId newId() {
        return new SettlementId(UUID.randomUUID());
    }

    public static SettlementId of(UUID value) {
        return new SettlementId(value);
    }

    public static SettlementId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SettlementId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SettlementId other)) {
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

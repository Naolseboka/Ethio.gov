package et.gov.ethgov.payments.domain.chargeback;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Chargeback. */
public final class ChargebackId {
    private final UUID value;

    private ChargebackId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ChargebackId newId() {
        return new ChargebackId(UUID.randomUUID());
    }

    public static ChargebackId of(UUID value) {
        return new ChargebackId(value);
    }

    public static ChargebackId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ChargebackId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChargebackId other)) {
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

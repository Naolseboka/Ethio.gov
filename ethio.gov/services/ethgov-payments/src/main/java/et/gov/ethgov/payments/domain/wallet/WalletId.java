package et.gov.ethgov.payments.domain.wallet;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Wallet. */
public final class WalletId {
    private final UUID value;

    private WalletId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static WalletId newId() {
        return new WalletId(UUID.randomUUID());
    }

    public static WalletId of(UUID value) {
        return new WalletId(value);
    }

    public static WalletId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new WalletId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WalletId other)) {
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

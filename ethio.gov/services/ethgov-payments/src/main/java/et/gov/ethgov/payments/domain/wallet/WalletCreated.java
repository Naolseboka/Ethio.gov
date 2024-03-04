package et.gov.ethgov.payments.domain.wallet;

import java.time.Instant;
import java.util.Objects;

/** Domain event: WalletCreated. */
public final class WalletCreated {
    private final WalletId id;
    private final Instant occurredAt;

    public WalletCreated(WalletId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public WalletId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

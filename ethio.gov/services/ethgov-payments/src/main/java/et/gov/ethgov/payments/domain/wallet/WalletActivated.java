package et.gov.ethgov.payments.domain.wallet;

import java.time.Instant;
import java.util.Objects;

/** Domain event: WalletActivated. */
public final class WalletActivated {
    private final WalletId id;
    private final Instant occurredAt;

    public WalletActivated(WalletId id, Instant occurredAt) {
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

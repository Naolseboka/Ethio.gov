package et.gov.ethgov.land.domain.transfer;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TransferClosed. */
public final class TransferClosed {
    private final TransferId id;
    private final Instant occurredAt;

    public TransferClosed(TransferId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public TransferId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.case.domain.transfer;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TransferCreated. */
public final class TransferCreated {
    private final TransferId id;
    private final Instant occurredAt;

    public TransferCreated(TransferId id, Instant occurredAt) {
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

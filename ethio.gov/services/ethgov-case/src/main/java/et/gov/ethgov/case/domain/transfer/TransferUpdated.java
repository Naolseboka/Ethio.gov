package et.gov.ethgov.case.domain.transfer;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TransferUpdated. */
public final class TransferUpdated {
    private final TransferId id;
    private final Instant occurredAt;

    public TransferUpdated(TransferId id, Instant occurredAt) {
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

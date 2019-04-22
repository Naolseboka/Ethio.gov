package et.gov.ethgov.case.domain.transfer;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TransferActivated. */
public final class TransferActivated {
    private final TransferId id;
    private final Instant occurredAt;

    public TransferActivated(TransferId id, Instant occurredAt) {
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

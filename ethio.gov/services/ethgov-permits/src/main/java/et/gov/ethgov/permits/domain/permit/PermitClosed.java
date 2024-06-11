package et.gov.ethgov.permits.domain.permit;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PermitClosed. */
public final class PermitClosed {
    private final PermitId id;
    private final Instant occurredAt;

    public PermitClosed(PermitId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PermitId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

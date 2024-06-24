package et.gov.ethgov.permits.domain.permitapplication;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PermitapplicationClosed. */
public final class PermitapplicationClosed {
    private final PermitapplicationId id;
    private final Instant occurredAt;

    public PermitapplicationClosed(PermitapplicationId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PermitapplicationId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

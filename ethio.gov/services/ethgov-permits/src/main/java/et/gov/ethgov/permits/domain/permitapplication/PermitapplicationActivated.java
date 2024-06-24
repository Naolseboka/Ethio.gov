package et.gov.ethgov.permits.domain.permitapplication;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PermitapplicationActivated. */
public final class PermitapplicationActivated {
    private final PermitapplicationId id;
    private final Instant occurredAt;

    public PermitapplicationActivated(PermitapplicationId id, Instant occurredAt) {
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

package et.gov.ethgov.licensing.domain.revocation;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RevocationUpdated. */
public final class RevocationUpdated {
    private final RevocationId id;
    private final Instant occurredAt;

    public RevocationUpdated(RevocationId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public RevocationId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

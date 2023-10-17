package et.gov.ethgov.organization.domain.seal;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SealActivated. */
public final class SealActivated {
    private final SealId id;
    private final Instant occurredAt;

    public SealActivated(SealId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SealId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

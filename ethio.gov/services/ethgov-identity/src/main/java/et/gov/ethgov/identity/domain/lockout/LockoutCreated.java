package et.gov.ethgov.identity.domain.lockout;

import java.time.Instant;
import java.util.Objects;

/** Domain event: LockoutCreated. */
public final class LockoutCreated {
    private final LockoutId id;
    private final Instant occurredAt;

    public LockoutCreated(LockoutId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public LockoutId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

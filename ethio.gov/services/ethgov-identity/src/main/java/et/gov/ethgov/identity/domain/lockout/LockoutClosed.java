package et.gov.ethgov.identity.domain.lockout;

import java.time.Instant;
import java.util.Objects;

/** Domain event: LockoutClosed. */
public final class LockoutClosed {
    private final LockoutId id;
    private final Instant occurredAt;

    public LockoutClosed(LockoutId id, Instant occurredAt) {
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

package et.gov.ethgov.identity.domain.passwordpolicy;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PasswordpolicySuspended. */
public final class PasswordpolicySuspended {
    private final PasswordpolicyId id;
    private final Instant occurredAt;

    public PasswordpolicySuspended(PasswordpolicyId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PasswordpolicyId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

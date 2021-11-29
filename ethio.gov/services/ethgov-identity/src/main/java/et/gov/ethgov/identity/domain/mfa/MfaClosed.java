package et.gov.ethgov.identity.domain.mfa;

import java.time.Instant;
import java.util.Objects;

/** Domain event: MfaClosed. */
public final class MfaClosed {
    private final MfaId id;
    private final Instant occurredAt;

    public MfaClosed(MfaId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public MfaId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.identity.domain.mfa;

import java.time.Instant;
import java.util.Objects;

/** Domain event: MfaUpdated. */
public final class MfaUpdated {
    private final MfaId id;
    private final Instant occurredAt;

    public MfaUpdated(MfaId id, Instant occurredAt) {
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

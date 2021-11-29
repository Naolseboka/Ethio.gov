package et.gov.ethgov.identity.domain.mfa;

import java.time.Instant;
import java.util.Objects;

/** Domain event: MfaActivated. */
public final class MfaActivated {
    private final MfaId id;
    private final Instant occurredAt;

    public MfaActivated(MfaId id, Instant occurredAt) {
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

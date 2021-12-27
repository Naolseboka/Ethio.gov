package et.gov.ethgov.identity.domain.session;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SessionSuspended. */
public final class SessionSuspended {
    private final SessionId id;
    private final Instant occurredAt;

    public SessionSuspended(SessionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SessionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

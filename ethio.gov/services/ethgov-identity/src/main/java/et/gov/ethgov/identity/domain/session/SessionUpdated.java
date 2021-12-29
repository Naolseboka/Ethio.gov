package et.gov.ethgov.identity.domain.session;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SessionUpdated. */
public final class SessionUpdated {
    private final SessionId id;
    private final Instant occurredAt;

    public SessionUpdated(SessionId id, Instant occurredAt) {
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

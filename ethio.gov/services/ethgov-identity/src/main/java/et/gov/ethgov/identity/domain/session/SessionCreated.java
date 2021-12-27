package et.gov.ethgov.identity.domain.session;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SessionCreated. */
public final class SessionCreated {
    private final SessionId id;
    private final Instant occurredAt;

    public SessionCreated(SessionId id, Instant occurredAt) {
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

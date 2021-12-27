package et.gov.ethgov.identity.domain.session;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SessionActivated. */
public final class SessionActivated {
    private final SessionId id;
    private final Instant occurredAt;

    public SessionActivated(SessionId id, Instant occurredAt) {
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

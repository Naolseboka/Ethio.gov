package et.gov.ethgov.identity.domain.oauthclient;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OauthclientClosed. */
public final class OauthclientClosed {
    private final OauthclientId id;
    private final Instant occurredAt;

    public OauthclientClosed(OauthclientId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public OauthclientId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

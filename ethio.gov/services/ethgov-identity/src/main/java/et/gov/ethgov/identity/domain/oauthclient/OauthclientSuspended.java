package et.gov.ethgov.identity.domain.oauthclient;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OauthclientSuspended. */
public final class OauthclientSuspended {
    private final OauthclientId id;
    private final Instant occurredAt;

    public OauthclientSuspended(OauthclientId id, Instant occurredAt) {
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

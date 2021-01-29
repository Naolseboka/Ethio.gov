package et.gov.ethgov.gateway.domain.authn;

import java.time.Instant;
import java.util.Objects;

/** Domain event: AuthnActivated. */
public final class AuthnActivated {
    private final AuthnId id;
    private final Instant occurredAt;

    public AuthnActivated(AuthnId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public AuthnId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

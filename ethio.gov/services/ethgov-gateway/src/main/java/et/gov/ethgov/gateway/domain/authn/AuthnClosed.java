package et.gov.ethgov.gateway.domain.authn;

import java.time.Instant;
import java.util.Objects;

/** Domain event: AuthnClosed. */
public final class AuthnClosed {
    private final AuthnId id;
    private final Instant occurredAt;

    public AuthnClosed(AuthnId id, Instant occurredAt) {
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

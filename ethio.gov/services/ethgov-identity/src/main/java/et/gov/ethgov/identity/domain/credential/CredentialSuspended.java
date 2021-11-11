package et.gov.ethgov.identity.domain.credential;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CredentialSuspended. */
public final class CredentialSuspended {
    private final CredentialId id;
    private final Instant occurredAt;

    public CredentialSuspended(CredentialId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CredentialId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

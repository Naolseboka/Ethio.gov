package et.gov.ethgov.identity.domain.credential;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CredentialActivated. */
public final class CredentialActivated {
    private final CredentialId id;
    private final Instant occurredAt;

    public CredentialActivated(CredentialId id, Instant occurredAt) {
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

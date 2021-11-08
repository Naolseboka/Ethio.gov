package et.gov.ethgov.identity.domain.credential;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CredentialClosed. */
public final class CredentialClosed {
    private final CredentialId id;
    private final Instant occurredAt;

    public CredentialClosed(CredentialId id, Instant occurredAt) {
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

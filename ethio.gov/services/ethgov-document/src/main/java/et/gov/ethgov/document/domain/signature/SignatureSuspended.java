package et.gov.ethgov.document.domain.signature;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SignatureSuspended. */
public final class SignatureSuspended {
    private final SignatureId id;
    private final Instant occurredAt;

    public SignatureSuspended(SignatureId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SignatureId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.document.domain.signature;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SignatureUpdated. */
public final class SignatureUpdated {
    private final SignatureId id;
    private final Instant occurredAt;

    public SignatureUpdated(SignatureId id, Instant occurredAt) {
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

package et.gov.ethgov.developer.domain.devcredential;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DevcredentialActivated. */
public final class DevcredentialActivated {
    private final DevcredentialId id;
    private final Instant occurredAt;

    public DevcredentialActivated(DevcredentialId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DevcredentialId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

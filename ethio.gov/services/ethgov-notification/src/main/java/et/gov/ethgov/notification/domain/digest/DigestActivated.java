package et.gov.ethgov.notification.domain.digest;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DigestActivated. */
public final class DigestActivated {
    private final DigestId id;
    private final Instant occurredAt;

    public DigestActivated(DigestId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DigestId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

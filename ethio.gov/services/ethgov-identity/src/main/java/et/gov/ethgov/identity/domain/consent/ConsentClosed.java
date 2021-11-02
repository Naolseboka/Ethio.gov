package et.gov.ethgov.identity.domain.consent;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ConsentClosed. */
public final class ConsentClosed {
    private final ConsentId id;
    private final Instant occurredAt;

    public ConsentClosed(ConsentId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ConsentId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

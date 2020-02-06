package et.gov.ethgov.developer.domain.supportticket;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SupportticketUpdated. */
public final class SupportticketUpdated {
    private final SupportticketId id;
    private final Instant occurredAt;

    public SupportticketUpdated(SupportticketId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SupportticketId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

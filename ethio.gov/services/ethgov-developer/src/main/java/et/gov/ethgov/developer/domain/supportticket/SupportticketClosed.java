package et.gov.ethgov.developer.domain.supportticket;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SupportticketClosed. */
public final class SupportticketClosed {
    private final SupportticketId id;
    private final Instant occurredAt;

    public SupportticketClosed(SupportticketId id, Instant occurredAt) {
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

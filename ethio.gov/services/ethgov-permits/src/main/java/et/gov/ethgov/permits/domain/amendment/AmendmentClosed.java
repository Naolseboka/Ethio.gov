package et.gov.ethgov.permits.domain.amendment;

import java.time.Instant;
import java.util.Objects;

/** Domain event: AmendmentClosed. */
public final class AmendmentClosed {
    private final AmendmentId id;
    private final Instant occurredAt;

    public AmendmentClosed(AmendmentId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public AmendmentId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

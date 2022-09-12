package et.gov.ethgov.licensing.domain.renewal;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RenewalClosed. */
public final class RenewalClosed {
    private final RenewalId id;
    private final Instant occurredAt;

    public RenewalClosed(RenewalId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public RenewalId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

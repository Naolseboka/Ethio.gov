package et.gov.ethgov.developer.domain.devratelimit;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DevratelimitActivated. */
public final class DevratelimitActivated {
    private final DevratelimitId id;
    private final Instant occurredAt;

    public DevratelimitActivated(DevratelimitId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DevratelimitId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

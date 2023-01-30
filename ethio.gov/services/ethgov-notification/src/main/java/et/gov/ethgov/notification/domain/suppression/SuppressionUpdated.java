package et.gov.ethgov.notification.domain.suppression;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SuppressionUpdated. */
public final class SuppressionUpdated {
    private final SuppressionId id;
    private final Instant occurredAt;

    public SuppressionUpdated(SuppressionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SuppressionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

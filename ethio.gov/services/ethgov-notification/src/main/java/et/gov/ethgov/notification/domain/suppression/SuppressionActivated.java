package et.gov.ethgov.notification.domain.suppression;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SuppressionActivated. */
public final class SuppressionActivated {
    private final SuppressionId id;
    private final Instant occurredAt;

    public SuppressionActivated(SuppressionId id, Instant occurredAt) {
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

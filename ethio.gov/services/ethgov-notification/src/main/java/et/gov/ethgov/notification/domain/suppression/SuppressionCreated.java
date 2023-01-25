package et.gov.ethgov.notification.domain.suppression;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SuppressionCreated. */
public final class SuppressionCreated {
    private final SuppressionId id;
    private final Instant occurredAt;

    public SuppressionCreated(SuppressionId id, Instant occurredAt) {
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

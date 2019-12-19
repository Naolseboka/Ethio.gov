package et.gov.ethgov.developer.domain.devapplication;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DevapplicationClosed. */
public final class DevapplicationClosed {
    private final DevapplicationId id;
    private final Instant occurredAt;

    public DevapplicationClosed(DevapplicationId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DevapplicationId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

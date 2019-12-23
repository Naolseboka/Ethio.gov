package et.gov.ethgov.developer.domain.devapplication;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DevapplicationSuspended. */
public final class DevapplicationSuspended {
    private final DevapplicationId id;
    private final Instant occurredAt;

    public DevapplicationSuspended(DevapplicationId id, Instant occurredAt) {
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

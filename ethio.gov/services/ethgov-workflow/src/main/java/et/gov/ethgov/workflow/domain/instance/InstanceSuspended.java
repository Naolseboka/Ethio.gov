package et.gov.ethgov.workflow.domain.instance;

import java.time.Instant;
import java.util.Objects;

/** Domain event: InstanceSuspended. */
public final class InstanceSuspended {
    private final InstanceId id;
    private final Instant occurredAt;

    public InstanceSuspended(InstanceId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public InstanceId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

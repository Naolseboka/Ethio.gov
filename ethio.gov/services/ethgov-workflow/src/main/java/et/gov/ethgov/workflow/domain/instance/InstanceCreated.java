package et.gov.ethgov.workflow.domain.instance;

import java.time.Instant;
import java.util.Objects;

/** Domain event: InstanceCreated. */
public final class InstanceCreated {
    private final InstanceId id;
    private final Instant occurredAt;

    public InstanceCreated(InstanceId id, Instant occurredAt) {
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

package et.gov.ethgov.workflow.domain.instance;

import java.time.Instant;
import java.util.Objects;

/** Domain event: InstanceClosed. */
public final class InstanceClosed {
    private final InstanceId id;
    private final Instant occurredAt;

    public InstanceClosed(InstanceId id, Instant occurredAt) {
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

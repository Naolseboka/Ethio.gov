package et.gov.ethgov.workflow.domain.gatewaynode;

import java.time.Instant;
import java.util.Objects;

/** Domain event: GatewaynodeCreated. */
public final class GatewaynodeCreated {
    private final GatewaynodeId id;
    private final Instant occurredAt;

    public GatewaynodeCreated(GatewaynodeId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public GatewaynodeId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

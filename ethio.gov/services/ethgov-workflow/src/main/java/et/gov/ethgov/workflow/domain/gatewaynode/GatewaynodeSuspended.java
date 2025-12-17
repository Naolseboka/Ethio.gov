package et.gov.ethgov.workflow.domain.gatewaynode;

import java.time.Instant;
import java.util.Objects;

/** Domain event: GatewaynodeSuspended. */
public final class GatewaynodeSuspended {
    private final GatewaynodeId id;
    private final Instant occurredAt;

    public GatewaynodeSuspended(GatewaynodeId id, Instant occurredAt) {
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

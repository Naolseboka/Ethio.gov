package et.gov.ethgov.gateway.domain.gwraterule;

import java.time.Instant;
import java.util.Objects;

/** Domain event: GwrateruleActivated. */
public final class GwrateruleActivated {
    private final GwrateruleId id;
    private final Instant occurredAt;

    public GwrateruleActivated(GwrateruleId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public GwrateruleId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

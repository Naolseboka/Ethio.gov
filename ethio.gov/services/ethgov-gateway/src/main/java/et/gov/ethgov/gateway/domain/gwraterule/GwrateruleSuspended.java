package et.gov.ethgov.gateway.domain.gwraterule;

import java.time.Instant;
import java.util.Objects;

/** Domain event: GwrateruleSuspended. */
public final class GwrateruleSuspended {
    private final GwrateruleId id;
    private final Instant occurredAt;

    public GwrateruleSuspended(GwrateruleId id, Instant occurredAt) {
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

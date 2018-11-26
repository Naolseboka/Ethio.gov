package et.gov.ethgov.analytics.domain.funnel;

import java.time.Instant;
import java.util.Objects;

/** Domain event: FunnelUpdated. */
public final class FunnelUpdated {
    private final FunnelId id;
    private final Instant occurredAt;

    public FunnelUpdated(FunnelId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public FunnelId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

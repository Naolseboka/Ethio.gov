package et.gov.ethgov.analytics.domain.funnel;

import java.time.Instant;
import java.util.Objects;

/** Domain event: FunnelActivated. */
public final class FunnelActivated {
    private final FunnelId id;
    private final Instant occurredAt;

    public FunnelActivated(FunnelId id, Instant occurredAt) {
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

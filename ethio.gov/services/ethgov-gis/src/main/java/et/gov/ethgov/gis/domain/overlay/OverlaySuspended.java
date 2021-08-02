package et.gov.ethgov.gis.domain.overlay;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OverlaySuspended. */
public final class OverlaySuspended {
    private final OverlayId id;
    private final Instant occurredAt;

    public OverlaySuspended(OverlayId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public OverlayId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

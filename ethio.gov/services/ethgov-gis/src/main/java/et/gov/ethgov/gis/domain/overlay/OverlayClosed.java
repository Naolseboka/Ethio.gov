package et.gov.ethgov.gis.domain.overlay;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OverlayClosed. */
public final class OverlayClosed {
    private final OverlayId id;
    private final Instant occurredAt;

    public OverlayClosed(OverlayId id, Instant occurredAt) {
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

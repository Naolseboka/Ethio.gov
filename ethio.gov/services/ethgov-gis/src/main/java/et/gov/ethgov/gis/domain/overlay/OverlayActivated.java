package et.gov.ethgov.gis.domain.overlay;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OverlayActivated. */
public final class OverlayActivated {
    private final OverlayId id;
    private final Instant occurredAt;

    public OverlayActivated(OverlayId id, Instant occurredAt) {
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

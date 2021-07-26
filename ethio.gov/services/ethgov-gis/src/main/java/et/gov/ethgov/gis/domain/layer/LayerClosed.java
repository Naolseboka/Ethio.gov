package et.gov.ethgov.gis.domain.layer;

import java.time.Instant;
import java.util.Objects;

/** Domain event: LayerClosed. */
public final class LayerClosed {
    private final LayerId id;
    private final Instant occurredAt;

    public LayerClosed(LayerId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public LayerId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

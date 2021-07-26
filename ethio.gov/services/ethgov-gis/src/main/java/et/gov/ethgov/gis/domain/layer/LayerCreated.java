package et.gov.ethgov.gis.domain.layer;

import java.time.Instant;
import java.util.Objects;

/** Domain event: LayerCreated. */
public final class LayerCreated {
    private final LayerId id;
    private final Instant occurredAt;

    public LayerCreated(LayerId id, Instant occurredAt) {
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

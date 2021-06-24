package et.gov.ethgov.gis.domain.crs;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CrsActivated. */
public final class CrsActivated {
    private final CrsId id;
    private final Instant occurredAt;

    public CrsActivated(CrsId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CrsId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

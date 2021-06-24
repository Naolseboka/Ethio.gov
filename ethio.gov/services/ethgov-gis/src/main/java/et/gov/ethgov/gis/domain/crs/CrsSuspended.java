package et.gov.ethgov.gis.domain.crs;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CrsSuspended. */
public final class CrsSuspended {
    private final CrsId id;
    private final Instant occurredAt;

    public CrsSuspended(CrsId id, Instant occurredAt) {
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

package et.gov.ethgov.opendata.domain.catalog;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CatalogSuspended. */
public final class CatalogSuspended {
    private final CatalogId id;
    private final Instant occurredAt;

    public CatalogSuspended(CatalogId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CatalogId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

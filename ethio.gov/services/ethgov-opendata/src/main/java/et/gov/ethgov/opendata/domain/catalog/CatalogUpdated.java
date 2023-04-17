package et.gov.ethgov.opendata.domain.catalog;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CatalogUpdated. */
public final class CatalogUpdated {
    private final CatalogId id;
    private final Instant occurredAt;

    public CatalogUpdated(CatalogId id, Instant occurredAt) {
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

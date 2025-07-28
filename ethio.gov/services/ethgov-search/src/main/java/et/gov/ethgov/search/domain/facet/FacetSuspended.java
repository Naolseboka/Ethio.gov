package et.gov.ethgov.search.domain.facet;

import java.time.Instant;
import java.util.Objects;

/** Domain event: FacetSuspended. */
public final class FacetSuspended {
    private final FacetId id;
    private final Instant occurredAt;

    public FacetSuspended(FacetId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public FacetId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

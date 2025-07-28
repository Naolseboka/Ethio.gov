package et.gov.ethgov.search.domain.facet;

import java.time.Instant;
import java.util.Objects;

/** Domain event: FacetUpdated. */
public final class FacetUpdated {
    private final FacetId id;
    private final Instant occurredAt;

    public FacetUpdated(FacetId id, Instant occurredAt) {
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

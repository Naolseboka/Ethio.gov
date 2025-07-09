package et.gov.ethgov.search.domain.collection;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CollectionSuspended. */
public final class CollectionSuspended {
    private final CollectionId id;
    private final Instant occurredAt;

    public CollectionSuspended(CollectionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CollectionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

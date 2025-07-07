package et.gov.ethgov.search.domain.collection;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CollectionClosed. */
public final class CollectionClosed {
    private final CollectionId id;
    private final Instant occurredAt;

    public CollectionClosed(CollectionId id, Instant occurredAt) {
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

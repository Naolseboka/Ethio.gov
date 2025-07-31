package et.gov.ethgov.search.domain.index;

import java.time.Instant;
import java.util.Objects;

/** Domain event: IndexCreated. */
public final class IndexCreated {
    private final IndexId id;
    private final Instant occurredAt;

    public IndexCreated(IndexId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public IndexId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

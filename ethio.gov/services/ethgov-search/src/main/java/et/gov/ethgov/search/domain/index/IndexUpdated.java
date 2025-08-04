package et.gov.ethgov.search.domain.index;

import java.time.Instant;
import java.util.Objects;

/** Domain event: IndexUpdated. */
public final class IndexUpdated {
    private final IndexId id;
    private final Instant occurredAt;

    public IndexUpdated(IndexId id, Instant occurredAt) {
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

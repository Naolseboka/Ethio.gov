package et.gov.ethgov.search.domain.query;

import java.time.Instant;
import java.util.Objects;

/** Domain event: QueryUpdated. */
public final class QueryUpdated {
    private final QueryId id;
    private final Instant occurredAt;

    public QueryUpdated(QueryId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public QueryId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

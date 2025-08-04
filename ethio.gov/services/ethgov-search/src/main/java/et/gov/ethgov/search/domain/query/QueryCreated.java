package et.gov.ethgov.search.domain.query;

import java.time.Instant;
import java.util.Objects;

/** Domain event: QueryCreated. */
public final class QueryCreated {
    private final QueryId id;
    private final Instant occurredAt;

    public QueryCreated(QueryId id, Instant occurredAt) {
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

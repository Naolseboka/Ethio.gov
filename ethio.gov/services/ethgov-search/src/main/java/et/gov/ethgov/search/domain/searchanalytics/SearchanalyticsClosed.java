package et.gov.ethgov.search.domain.searchanalytics;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SearchanalyticsClosed. */
public final class SearchanalyticsClosed {
    private final SearchanalyticsId id;
    private final Instant occurredAt;

    public SearchanalyticsClosed(SearchanalyticsId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SearchanalyticsId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

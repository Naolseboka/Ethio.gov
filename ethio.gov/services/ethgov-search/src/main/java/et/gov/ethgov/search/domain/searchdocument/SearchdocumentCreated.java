package et.gov.ethgov.search.domain.searchdocument;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SearchdocumentCreated. */
public final class SearchdocumentCreated {
    private final SearchdocumentId id;
    private final Instant occurredAt;

    public SearchdocumentCreated(SearchdocumentId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SearchdocumentId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

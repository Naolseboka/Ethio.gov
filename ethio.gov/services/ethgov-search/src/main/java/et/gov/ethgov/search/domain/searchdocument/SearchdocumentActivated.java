package et.gov.ethgov.search.domain.searchdocument;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SearchdocumentActivated. */
public final class SearchdocumentActivated {
    private final SearchdocumentId id;
    private final Instant occurredAt;

    public SearchdocumentActivated(SearchdocumentId id, Instant occurredAt) {
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

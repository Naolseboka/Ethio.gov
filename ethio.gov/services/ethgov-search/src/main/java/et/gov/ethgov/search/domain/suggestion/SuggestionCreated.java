package et.gov.ethgov.search.domain.suggestion;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SuggestionCreated. */
public final class SuggestionCreated {
    private final SuggestionId id;
    private final Instant occurredAt;

    public SuggestionCreated(SuggestionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SuggestionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

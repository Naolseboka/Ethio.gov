package et.gov.ethgov.search.domain.synonym;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SynonymActivated. */
public final class SynonymActivated {
    private final SynonymId id;
    private final Instant occurredAt;

    public SynonymActivated(SynonymId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SynonymId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

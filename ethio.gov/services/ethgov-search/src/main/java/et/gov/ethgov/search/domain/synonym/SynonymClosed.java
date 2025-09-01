package et.gov.ethgov.search.domain.synonym;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SynonymClosed. */
public final class SynonymClosed {
    private final SynonymId id;
    private final Instant occurredAt;

    public SynonymClosed(SynonymId id, Instant occurredAt) {
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

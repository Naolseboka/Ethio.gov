package et.gov.ethgov.search.domain.crawljob;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CrawljobUpdated. */
public final class CrawljobUpdated {
    private final CrawljobId id;
    private final Instant occurredAt;

    public CrawljobUpdated(CrawljobId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CrawljobId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

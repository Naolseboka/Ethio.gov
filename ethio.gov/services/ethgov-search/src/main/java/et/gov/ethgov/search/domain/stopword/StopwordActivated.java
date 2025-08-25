package et.gov.ethgov.search.domain.stopword;

import java.time.Instant;
import java.util.Objects;

/** Domain event: StopwordActivated. */
public final class StopwordActivated {
    private final StopwordId id;
    private final Instant occurredAt;

    public StopwordActivated(StopwordId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public StopwordId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

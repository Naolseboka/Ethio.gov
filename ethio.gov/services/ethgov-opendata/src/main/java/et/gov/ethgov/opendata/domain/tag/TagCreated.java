package et.gov.ethgov.opendata.domain.tag;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TagCreated. */
public final class TagCreated {
    private final TagId id;
    private final Instant occurredAt;

    public TagCreated(TagId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public TagId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

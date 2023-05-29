package et.gov.ethgov.opendata.domain.publisher;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PublisherSuspended. */
public final class PublisherSuspended {
    private final PublisherId id;
    private final Instant occurredAt;

    public PublisherSuspended(PublisherId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PublisherId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.opendata.domain.publisher;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PublisherUpdated. */
public final class PublisherUpdated {
    private final PublisherId id;
    private final Instant occurredAt;

    public PublisherUpdated(PublisherId id, Instant occurredAt) {
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

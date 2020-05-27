package et.gov.ethgov.directory.domain.publiclisting;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PubliclistingUpdated. */
public final class PubliclistingUpdated {
    private final PubliclistingId id;
    private final Instant occurredAt;

    public PubliclistingUpdated(PubliclistingId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PubliclistingId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

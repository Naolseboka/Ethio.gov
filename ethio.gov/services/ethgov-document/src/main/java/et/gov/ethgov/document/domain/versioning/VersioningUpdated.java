package et.gov.ethgov.document.domain.versioning;

import java.time.Instant;
import java.util.Objects;

/** Domain event: VersioningUpdated. */
public final class VersioningUpdated {
    private final VersioningId id;
    private final Instant occurredAt;

    public VersioningUpdated(VersioningId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public VersioningId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

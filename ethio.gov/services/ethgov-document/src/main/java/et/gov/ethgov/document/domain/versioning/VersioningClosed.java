package et.gov.ethgov.document.domain.versioning;

import java.time.Instant;
import java.util.Objects;

/** Domain event: VersioningClosed. */
public final class VersioningClosed {
    private final VersioningId id;
    private final Instant occurredAt;

    public VersioningClosed(VersioningId id, Instant occurredAt) {
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

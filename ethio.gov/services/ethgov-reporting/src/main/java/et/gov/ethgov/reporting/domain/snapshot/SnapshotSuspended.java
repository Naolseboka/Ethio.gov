package et.gov.ethgov.reporting.domain.snapshot;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SnapshotSuspended. */
public final class SnapshotSuspended {
    private final SnapshotId id;
    private final Instant occurredAt;

    public SnapshotSuspended(SnapshotId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SnapshotId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.document.domain.archivebox;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ArchiveboxSuspended. */
public final class ArchiveboxSuspended {
    private final ArchiveboxId id;
    private final Instant occurredAt;

    public ArchiveboxSuspended(ArchiveboxId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ArchiveboxId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

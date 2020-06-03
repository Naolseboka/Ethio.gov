package et.gov.ethgov.directory.domain.regionnode;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RegionnodeClosed. */
public final class RegionnodeClosed {
    private final RegionnodeId id;
    private final Instant occurredAt;

    public RegionnodeClosed(RegionnodeId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public RegionnodeId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

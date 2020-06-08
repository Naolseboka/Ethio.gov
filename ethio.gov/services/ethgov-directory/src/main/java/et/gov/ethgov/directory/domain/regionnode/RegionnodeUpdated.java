package et.gov.ethgov.directory.domain.regionnode;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RegionnodeUpdated. */
public final class RegionnodeUpdated {
    private final RegionnodeId id;
    private final Instant occurredAt;

    public RegionnodeUpdated(RegionnodeId id, Instant occurredAt) {
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

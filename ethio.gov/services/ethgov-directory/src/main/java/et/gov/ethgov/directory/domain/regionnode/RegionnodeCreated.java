package et.gov.ethgov.directory.domain.regionnode;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RegionnodeCreated. */
public final class RegionnodeCreated {
    private final RegionnodeId id;
    private final Instant occurredAt;

    public RegionnodeCreated(RegionnodeId id, Instant occurredAt) {
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

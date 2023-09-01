package et.gov.ethgov.organization.domain.hierarchy;

import java.time.Instant;
import java.util.Objects;

/** Domain event: HierarchySuspended. */
public final class HierarchySuspended {
    private final HierarchyId id;
    private final Instant occurredAt;

    public HierarchySuspended(HierarchyId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public HierarchyId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

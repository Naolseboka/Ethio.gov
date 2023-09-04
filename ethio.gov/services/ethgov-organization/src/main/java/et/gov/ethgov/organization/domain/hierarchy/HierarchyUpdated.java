package et.gov.ethgov.organization.domain.hierarchy;

import java.time.Instant;
import java.util.Objects;

/** Domain event: HierarchyUpdated. */
public final class HierarchyUpdated {
    private final HierarchyId id;
    private final Instant occurredAt;

    public HierarchyUpdated(HierarchyId id, Instant occurredAt) {
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

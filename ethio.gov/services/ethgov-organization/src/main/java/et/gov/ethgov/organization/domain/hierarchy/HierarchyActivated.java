package et.gov.ethgov.organization.domain.hierarchy;

import java.time.Instant;
import java.util.Objects;

/** Domain event: HierarchyActivated. */
public final class HierarchyActivated {
    private final HierarchyId id;
    private final Instant occurredAt;

    public HierarchyActivated(HierarchyId id, Instant occurredAt) {
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

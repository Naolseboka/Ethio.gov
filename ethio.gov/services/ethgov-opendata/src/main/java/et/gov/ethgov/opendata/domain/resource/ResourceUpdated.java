package et.gov.ethgov.opendata.domain.resource;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ResourceUpdated. */
public final class ResourceUpdated {
    private final ResourceId id;
    private final Instant occurredAt;

    public ResourceUpdated(ResourceId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ResourceId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.opendata.domain.resource;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ResourceClosed. */
public final class ResourceClosed {
    private final ResourceId id;
    private final Instant occurredAt;

    public ResourceClosed(ResourceId id, Instant occurredAt) {
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

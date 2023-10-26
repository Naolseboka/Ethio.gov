package et.gov.ethgov.organization.domain.servicecatalog;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ServicecatalogUpdated. */
public final class ServicecatalogUpdated {
    private final ServicecatalogId id;
    private final Instant occurredAt;

    public ServicecatalogUpdated(ServicecatalogId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ServicecatalogId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

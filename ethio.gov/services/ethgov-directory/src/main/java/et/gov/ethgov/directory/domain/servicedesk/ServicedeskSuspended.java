package et.gov.ethgov.directory.domain.servicedesk;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ServicedeskSuspended. */
public final class ServicedeskSuspended {
    private final ServicedeskId id;
    private final Instant occurredAt;

    public ServicedeskSuspended(ServicedeskId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ServicedeskId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

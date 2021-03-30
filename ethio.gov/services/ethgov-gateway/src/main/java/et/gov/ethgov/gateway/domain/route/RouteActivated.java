package et.gov.ethgov.gateway.domain.route;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RouteActivated. */
public final class RouteActivated {
    private final RouteId id;
    private final Instant occurredAt;

    public RouteActivated(RouteId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public RouteId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

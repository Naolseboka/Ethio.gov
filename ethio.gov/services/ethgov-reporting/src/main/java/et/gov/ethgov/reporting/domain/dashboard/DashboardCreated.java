package et.gov.ethgov.reporting.domain.dashboard;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DashboardCreated. */
public final class DashboardCreated {
    private final DashboardId id;
    private final Instant occurredAt;

    public DashboardCreated(DashboardId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DashboardId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

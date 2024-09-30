package et.gov.ethgov.reporting.domain.dashboard;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DashboardClosed. */
public final class DashboardClosed {
    private final DashboardId id;
    private final Instant occurredAt;

    public DashboardClosed(DashboardId id, Instant occurredAt) {
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

package et.gov.ethgov.case.domain.appeal;

import java.time.Instant;
import java.util.Objects;

/** Domain event: AppealSuspended. */
public final class AppealSuspended {
    private final AppealId id;
    private final Instant occurredAt;

    public AppealSuspended(AppealId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public AppealId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

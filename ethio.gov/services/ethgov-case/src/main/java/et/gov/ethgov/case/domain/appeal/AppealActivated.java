package et.gov.ethgov.case.domain.appeal;

import java.time.Instant;
import java.util.Objects;

/** Domain event: AppealActivated. */
public final class AppealActivated {
    private final AppealId id;
    private final Instant occurredAt;

    public AppealActivated(AppealId id, Instant occurredAt) {
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

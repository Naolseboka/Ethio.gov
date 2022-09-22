package et.gov.ethgov.licensing.domain.suspension;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SuspensionSuspended. */
public final class SuspensionSuspended {
    private final SuspensionId id;
    private final Instant occurredAt;

    public SuspensionSuspended(SuspensionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SuspensionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.licensing.domain.suspension;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SuspensionClosed. */
public final class SuspensionClosed {
    private final SuspensionId id;
    private final Instant occurredAt;

    public SuspensionClosed(SuspensionId id, Instant occurredAt) {
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

package et.gov.ethgov.developer.domain.usage;

import java.time.Instant;
import java.util.Objects;

/** Domain event: UsageSuspended. */
public final class UsageSuspended {
    private final UsageId id;
    private final Instant occurredAt;

    public UsageSuspended(UsageId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public UsageId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

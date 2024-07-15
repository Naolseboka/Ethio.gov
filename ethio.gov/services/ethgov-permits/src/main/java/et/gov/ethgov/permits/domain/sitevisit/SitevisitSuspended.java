package et.gov.ethgov.permits.domain.sitevisit;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SitevisitSuspended. */
public final class SitevisitSuspended {
    private final SitevisitId id;
    private final Instant occurredAt;

    public SitevisitSuspended(SitevisitId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SitevisitId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.licensing.domain.application;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ApplicationSuspended. */
public final class ApplicationSuspended {
    private final ApplicationId id;
    private final Instant occurredAt;

    public ApplicationSuspended(ApplicationId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ApplicationId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

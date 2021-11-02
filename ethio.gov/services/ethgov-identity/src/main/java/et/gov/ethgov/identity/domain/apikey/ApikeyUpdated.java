package et.gov.ethgov.identity.domain.apikey;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ApikeyUpdated. */
public final class ApikeyUpdated {
    private final ApikeyId id;
    private final Instant occurredAt;

    public ApikeyUpdated(ApikeyId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ApikeyId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

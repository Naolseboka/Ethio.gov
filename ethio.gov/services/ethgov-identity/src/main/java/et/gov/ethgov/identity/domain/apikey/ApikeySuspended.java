package et.gov.ethgov.identity.domain.apikey;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ApikeySuspended. */
public final class ApikeySuspended {
    private final ApikeyId id;
    private final Instant occurredAt;

    public ApikeySuspended(ApikeyId id, Instant occurredAt) {
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

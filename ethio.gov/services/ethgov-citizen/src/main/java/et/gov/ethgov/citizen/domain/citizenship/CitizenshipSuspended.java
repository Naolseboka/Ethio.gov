package et.gov.ethgov.citizen.domain.citizenship;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CitizenshipSuspended. */
public final class CitizenshipSuspended {
    private final CitizenshipId id;
    private final Instant occurredAt;

    public CitizenshipSuspended(CitizenshipId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CitizenshipId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

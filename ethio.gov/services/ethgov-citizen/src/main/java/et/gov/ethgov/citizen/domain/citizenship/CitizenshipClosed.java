package et.gov.ethgov.citizen.domain.citizenship;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CitizenshipClosed. */
public final class CitizenshipClosed {
    private final CitizenshipId id;
    private final Instant occurredAt;

    public CitizenshipClosed(CitizenshipId id, Instant occurredAt) {
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

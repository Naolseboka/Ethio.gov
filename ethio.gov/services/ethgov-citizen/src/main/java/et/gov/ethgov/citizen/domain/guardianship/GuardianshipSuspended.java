package et.gov.ethgov.citizen.domain.guardianship;

import java.time.Instant;
import java.util.Objects;

/** Domain event: GuardianshipSuspended. */
public final class GuardianshipSuspended {
    private final GuardianshipId id;
    private final Instant occurredAt;

    public GuardianshipSuspended(GuardianshipId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public GuardianshipId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

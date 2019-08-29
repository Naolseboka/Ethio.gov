package et.gov.ethgov.citizen.domain.guardianship;

import java.time.Instant;
import java.util.Objects;

/** Domain event: GuardianshipCreated. */
public final class GuardianshipCreated {
    private final GuardianshipId id;
    private final Instant occurredAt;

    public GuardianshipCreated(GuardianshipId id, Instant occurredAt) {
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

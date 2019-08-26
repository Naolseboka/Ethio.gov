package et.gov.ethgov.citizen.domain.emergencycontact;

import java.time.Instant;
import java.util.Objects;

/** Domain event: EmergencycontactCreated. */
public final class EmergencycontactCreated {
    private final EmergencycontactId id;
    private final Instant occurredAt;

    public EmergencycontactCreated(EmergencycontactId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public EmergencycontactId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

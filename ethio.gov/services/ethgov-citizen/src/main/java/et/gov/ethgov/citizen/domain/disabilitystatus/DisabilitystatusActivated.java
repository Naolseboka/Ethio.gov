package et.gov.ethgov.citizen.domain.disabilitystatus;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DisabilitystatusActivated. */
public final class DisabilitystatusActivated {
    private final DisabilitystatusId id;
    private final Instant occurredAt;

    public DisabilitystatusActivated(DisabilitystatusId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DisabilitystatusId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

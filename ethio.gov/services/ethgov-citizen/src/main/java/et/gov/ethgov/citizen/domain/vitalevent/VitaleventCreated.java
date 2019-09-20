package et.gov.ethgov.citizen.domain.vitalevent;

import java.time.Instant;
import java.util.Objects;

/** Domain event: VitaleventCreated. */
public final class VitaleventCreated {
    private final VitaleventId id;
    private final Instant occurredAt;

    public VitaleventCreated(VitaleventId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public VitaleventId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

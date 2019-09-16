package et.gov.ethgov.citizen.domain.residency;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ResidencyActivated. */
public final class ResidencyActivated {
    private final ResidencyId id;
    private final Instant occurredAt;

    public ResidencyActivated(ResidencyId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ResidencyId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

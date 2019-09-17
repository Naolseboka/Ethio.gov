package et.gov.ethgov.citizen.domain.residency;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ResidencySuspended. */
public final class ResidencySuspended {
    private final ResidencyId id;
    private final Instant occurredAt;

    public ResidencySuspended(ResidencyId id, Instant occurredAt) {
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

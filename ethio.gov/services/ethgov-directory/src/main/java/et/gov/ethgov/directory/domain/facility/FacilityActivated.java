package et.gov.ethgov.directory.domain.facility;

import java.time.Instant;
import java.util.Objects;

/** Domain event: FacilityActivated. */
public final class FacilityActivated {
    private final FacilityId id;
    private final Instant occurredAt;

    public FacilityActivated(FacilityId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public FacilityId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

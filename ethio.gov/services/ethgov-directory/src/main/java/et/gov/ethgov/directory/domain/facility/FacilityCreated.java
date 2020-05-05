package et.gov.ethgov.directory.domain.facility;

import java.time.Instant;
import java.util.Objects;

/** Domain event: FacilityCreated. */
public final class FacilityCreated {
    private final FacilityId id;
    private final Instant occurredAt;

    public FacilityCreated(FacilityId id, Instant occurredAt) {
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

package et.gov.ethgov.land.domain.parcel;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ParcelActivated. */
public final class ParcelActivated {
    private final ParcelId id;
    private final Instant occurredAt;

    public ParcelActivated(ParcelId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ParcelId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

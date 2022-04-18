package et.gov.ethgov.land.domain.parcel;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ParcelSuspended. */
public final class ParcelSuspended {
    private final ParcelId id;
    private final Instant occurredAt;

    public ParcelSuspended(ParcelId id, Instant occurredAt) {
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

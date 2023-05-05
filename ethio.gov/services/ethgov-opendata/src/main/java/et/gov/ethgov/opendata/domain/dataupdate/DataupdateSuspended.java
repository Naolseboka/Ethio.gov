package et.gov.ethgov.opendata.domain.dataupdate;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DataupdateSuspended. */
public final class DataupdateSuspended {
    private final DataupdateId id;
    private final Instant occurredAt;

    public DataupdateSuspended(DataupdateId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DataupdateId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

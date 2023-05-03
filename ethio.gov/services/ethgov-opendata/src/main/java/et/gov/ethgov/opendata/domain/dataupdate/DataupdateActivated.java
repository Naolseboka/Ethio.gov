package et.gov.ethgov.opendata.domain.dataupdate;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DataupdateActivated. */
public final class DataupdateActivated {
    private final DataupdateId id;
    private final Instant occurredAt;

    public DataupdateActivated(DataupdateId id, Instant occurredAt) {
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

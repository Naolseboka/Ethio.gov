package et.gov.ethgov.reporting.domain.dataset;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DatasetCreated. */
public final class DatasetCreated {
    private final DatasetId id;
    private final Instant occurredAt;

    public DatasetCreated(DatasetId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DatasetId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

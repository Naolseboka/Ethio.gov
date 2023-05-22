package et.gov.ethgov.opendata.domain.opendataset;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OpendatasetUpdated. */
public final class OpendatasetUpdated {
    private final OpendatasetId id;
    private final Instant occurredAt;

    public OpendatasetUpdated(OpendatasetId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public OpendatasetId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

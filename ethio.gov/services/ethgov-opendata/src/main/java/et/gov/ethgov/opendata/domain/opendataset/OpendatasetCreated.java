package et.gov.ethgov.opendata.domain.opendataset;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OpendatasetCreated. */
public final class OpendatasetCreated {
    private final OpendatasetId id;
    private final Instant occurredAt;

    public OpendatasetCreated(OpendatasetId id, Instant occurredAt) {
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

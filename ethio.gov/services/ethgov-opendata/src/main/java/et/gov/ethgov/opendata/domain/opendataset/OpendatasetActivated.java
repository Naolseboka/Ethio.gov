package et.gov.ethgov.opendata.domain.opendataset;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OpendatasetActivated. */
public final class OpendatasetActivated {
    private final OpendatasetId id;
    private final Instant occurredAt;

    public OpendatasetActivated(OpendatasetId id, Instant occurredAt) {
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

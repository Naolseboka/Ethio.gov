package et.gov.ethgov.analytics.domain.pipeline;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PipelineUpdated. */
public final class PipelineUpdated {
    private final PipelineId id;
    private final Instant occurredAt;

    public PipelineUpdated(PipelineId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PipelineId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

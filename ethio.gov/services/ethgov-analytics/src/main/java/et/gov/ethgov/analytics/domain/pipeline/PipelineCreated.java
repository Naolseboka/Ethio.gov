package et.gov.ethgov.analytics.domain.pipeline;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PipelineCreated. */
public final class PipelineCreated {
    private final PipelineId id;
    private final Instant occurredAt;

    public PipelineCreated(PipelineId id, Instant occurredAt) {
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

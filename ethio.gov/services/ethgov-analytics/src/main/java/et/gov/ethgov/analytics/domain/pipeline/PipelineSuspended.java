package et.gov.ethgov.analytics.domain.pipeline;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PipelineSuspended. */
public final class PipelineSuspended {
    private final PipelineId id;
    private final Instant occurredAt;

    public PipelineSuspended(PipelineId id, Instant occurredAt) {
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

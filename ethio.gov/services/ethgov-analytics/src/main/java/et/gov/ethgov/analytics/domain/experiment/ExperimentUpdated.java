package et.gov.ethgov.analytics.domain.experiment;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ExperimentUpdated. */
public final class ExperimentUpdated {
    private final ExperimentId id;
    private final Instant occurredAt;

    public ExperimentUpdated(ExperimentId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ExperimentId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

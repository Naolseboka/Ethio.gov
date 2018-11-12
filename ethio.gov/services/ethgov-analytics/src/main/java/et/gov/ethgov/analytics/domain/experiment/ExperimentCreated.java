package et.gov.ethgov.analytics.domain.experiment;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ExperimentCreated. */
public final class ExperimentCreated {
    private final ExperimentId id;
    private final Instant occurredAt;

    public ExperimentCreated(ExperimentId id, Instant occurredAt) {
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

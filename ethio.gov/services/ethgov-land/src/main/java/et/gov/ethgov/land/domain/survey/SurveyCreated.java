package et.gov.ethgov.land.domain.survey;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SurveyCreated. */
public final class SurveyCreated {
    private final SurveyId id;
    private final Instant occurredAt;

    public SurveyCreated(SurveyId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SurveyId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.land.domain.survey;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SurveyUpdated. */
public final class SurveyUpdated {
    private final SurveyId id;
    private final Instant occurredAt;

    public SurveyUpdated(SurveyId id, Instant occurredAt) {
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

package et.gov.ethgov.analytics.domain.forecast;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ForecastCreated. */
public final class ForecastCreated {
    private final ForecastId id;
    private final Instant occurredAt;

    public ForecastCreated(ForecastId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ForecastId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}

package et.gov.ethgov.analytics.domain.forecast;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ForecastActivated. */
public final class ForecastActivated {
    private final ForecastId id;
    private final Instant occurredAt;

    public ForecastActivated(ForecastId id, Instant occurredAt) {
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

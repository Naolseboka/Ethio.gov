package et.gov.ethgov.analytics.application.forecast;

import et.gov.ethgov.analytics.domain.forecast.Forecast;
import et.gov.ethgov.analytics.domain.forecast.ForecastId;
import et.gov.ethgov.analytics.domain.forecast.ForecastRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendForecastUseCase {
    private final ForecastRepository repository;
    private final Clock clock;

    public SuspendForecastUseCase(ForecastRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ForecastId execute(SuspendForecastCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ForecastId id = ForecastId.parse(command.forecastId());
        Forecast aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("forecast not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendForecastCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String forecastId,
            String reason) {}
}

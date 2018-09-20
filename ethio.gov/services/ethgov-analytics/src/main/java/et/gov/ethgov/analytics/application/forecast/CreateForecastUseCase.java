package et.gov.ethgov.analytics.application.forecast;

import et.gov.ethgov.analytics.domain.forecast.Forecast;
import et.gov.ethgov.analytics.domain.forecast.ForecastId;
import et.gov.ethgov.analytics.domain.forecast.ForecastRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateForecastUseCase {
    private final ForecastRepository repository;
    private final Clock clock;

    public CreateForecastUseCase(ForecastRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ForecastId execute(CreateForecastCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Forecast aggregate = Forecast.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateForecastCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String forecastId,
            String reason) {}
}

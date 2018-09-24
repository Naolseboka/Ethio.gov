package et.gov.ethgov.analytics.application.forecast;

import et.gov.ethgov.analytics.domain.forecast.Forecast;
import et.gov.ethgov.analytics.domain.forecast.ForecastId;
import et.gov.ethgov.analytics.domain.forecast.ForecastRepository;
import java.util.Objects;

public final class GetForecastUseCase {
    private final ForecastRepository repository;

    public GetForecastUseCase(ForecastRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Forecast execute(String rawId) {
        ForecastId id = ForecastId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("forecast not found: " + rawId));
    }
}

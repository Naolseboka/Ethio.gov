package et.gov.ethgov.analytics.application.forecast;

import et.gov.ethgov.analytics.domain.forecast.Forecast;
import et.gov.ethgov.analytics.domain.forecast.ForecastRepository;
import java.util.List;
import java.util.Objects;

public final class ListForecastUseCase {
    private final ForecastRepository repository;

    public ListForecastUseCase(ForecastRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Forecast> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}

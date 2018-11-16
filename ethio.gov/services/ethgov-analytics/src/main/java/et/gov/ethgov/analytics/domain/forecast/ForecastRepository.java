package et.gov.ethgov.analytics.domain.forecast;

import java.util.List;
import java.util.Optional;

public interface ForecastRepository {
    Forecast save(Forecast aggregate);

    Optional<Forecast> findById(ForecastId id);

    Optional<Forecast> findByReferenceCode(String tenantId, String referenceCode);

    List<Forecast> findByTenant(String tenantId, int limit);

    void delete(ForecastId id);
}

package et.gov.ethgov.search.domain.searchanalytics;

import java.util.List;
import java.util.Optional;

public interface SearchanalyticsRepository {
    Searchanalytics save(Searchanalytics aggregate);

    Optional<Searchanalytics> findById(SearchanalyticsId id);

    Optional<Searchanalytics> findByReferenceCode(String tenantId, String referenceCode);

    List<Searchanalytics> findByTenant(String tenantId, int limit);

    void delete(SearchanalyticsId id);
}

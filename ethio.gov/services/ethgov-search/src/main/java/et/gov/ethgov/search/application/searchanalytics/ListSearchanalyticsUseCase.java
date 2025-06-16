package et.gov.ethgov.search.application.searchanalytics;

import et.gov.ethgov.search.domain.searchanalytics.Searchanalytics;
import et.gov.ethgov.search.domain.searchanalytics.SearchanalyticsRepository;
import java.util.List;
import java.util.Objects;

public final class ListSearchanalyticsUseCase {
    private final SearchanalyticsRepository repository;

    public ListSearchanalyticsUseCase(SearchanalyticsRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Searchanalytics> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}

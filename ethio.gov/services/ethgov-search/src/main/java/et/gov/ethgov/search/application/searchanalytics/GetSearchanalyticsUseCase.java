package et.gov.ethgov.search.application.searchanalytics;

import et.gov.ethgov.search.domain.searchanalytics.Searchanalytics;
import et.gov.ethgov.search.domain.searchanalytics.SearchanalyticsId;
import et.gov.ethgov.search.domain.searchanalytics.SearchanalyticsRepository;
import java.util.Objects;

public final class GetSearchanalyticsUseCase {
    private final SearchanalyticsRepository repository;

    public GetSearchanalyticsUseCase(SearchanalyticsRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Searchanalytics execute(String rawId) {
        SearchanalyticsId id = SearchanalyticsId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("searchanalytics not found: " + rawId));
    }
}

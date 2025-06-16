package et.gov.ethgov.search.application.searchanalytics;

import et.gov.ethgov.search.domain.searchanalytics.Searchanalytics;
import et.gov.ethgov.search.domain.searchanalytics.SearchanalyticsId;
import et.gov.ethgov.search.domain.searchanalytics.SearchanalyticsRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendSearchanalyticsUseCase {
    private final SearchanalyticsRepository repository;
    private final Clock clock;

    public SuspendSearchanalyticsUseCase(SearchanalyticsRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SearchanalyticsId execute(SuspendSearchanalyticsCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SearchanalyticsId id = SearchanalyticsId.parse(command.searchanalyticsId());
        Searchanalytics aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("searchanalytics not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendSearchanalyticsCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String searchanalyticsId,
            String reason) {}
}

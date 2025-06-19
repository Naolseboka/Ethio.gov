package et.gov.ethgov.search.application.searchanalytics;

import et.gov.ethgov.search.domain.searchanalytics.Searchanalytics;
import et.gov.ethgov.search.domain.searchanalytics.SearchanalyticsId;
import et.gov.ethgov.search.domain.searchanalytics.SearchanalyticsRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateSearchanalyticsUseCase {
    private final SearchanalyticsRepository repository;
    private final Clock clock;

    public UpdateSearchanalyticsUseCase(SearchanalyticsRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SearchanalyticsId execute(UpdateSearchanalyticsCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SearchanalyticsId id = SearchanalyticsId.parse(command.searchanalyticsId());
        Searchanalytics aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("searchanalytics not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateSearchanalyticsCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String searchanalyticsId,
            String reason) {}
}

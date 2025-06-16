package et.gov.ethgov.search.application.searchanalytics;

import et.gov.ethgov.search.domain.searchanalytics.Searchanalytics;
import et.gov.ethgov.search.domain.searchanalytics.SearchanalyticsId;
import et.gov.ethgov.search.domain.searchanalytics.SearchanalyticsRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSearchanalyticsUseCase {
    private final SearchanalyticsRepository repository;
    private final Clock clock;

    public CreateSearchanalyticsUseCase(SearchanalyticsRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SearchanalyticsId execute(CreateSearchanalyticsCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Searchanalytics aggregate = Searchanalytics.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSearchanalyticsCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String searchanalyticsId,
            String reason) {}
}

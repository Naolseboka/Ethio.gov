package et.gov.ethgov.search.application.query;

import et.gov.ethgov.search.domain.query.Query;
import et.gov.ethgov.search.domain.query.QueryId;
import et.gov.ethgov.search.domain.query.QueryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateQueryUseCase {
    private final QueryRepository repository;
    private final Clock clock;

    public ActivateQueryUseCase(QueryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public QueryId execute(ActivateQueryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        QueryId id = QueryId.parse(command.queryId());
        Query aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("query not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateQueryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String queryId,
            String reason) {}
}

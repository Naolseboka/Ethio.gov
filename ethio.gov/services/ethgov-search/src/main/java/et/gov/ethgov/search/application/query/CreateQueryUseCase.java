package et.gov.ethgov.search.application.query;

import et.gov.ethgov.search.domain.query.Query;
import et.gov.ethgov.search.domain.query.QueryId;
import et.gov.ethgov.search.domain.query.QueryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateQueryUseCase {
    private final QueryRepository repository;
    private final Clock clock;

    public CreateQueryUseCase(QueryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public QueryId execute(CreateQueryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Query aggregate = Query.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateQueryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String queryId,
            String reason) {}
}

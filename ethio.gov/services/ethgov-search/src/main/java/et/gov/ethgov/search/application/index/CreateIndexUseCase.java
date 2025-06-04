package et.gov.ethgov.search.application.index;

import et.gov.ethgov.search.domain.index.Index;
import et.gov.ethgov.search.domain.index.IndexId;
import et.gov.ethgov.search.domain.index.IndexRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateIndexUseCase {
    private final IndexRepository repository;
    private final Clock clock;

    public CreateIndexUseCase(IndexRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public IndexId execute(CreateIndexCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Index aggregate = Index.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateIndexCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String indexId,
            String reason) {}
}

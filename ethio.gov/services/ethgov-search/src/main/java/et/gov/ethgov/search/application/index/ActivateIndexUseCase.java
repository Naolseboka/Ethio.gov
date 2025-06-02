package et.gov.ethgov.search.application.index;

import et.gov.ethgov.search.domain.index.Index;
import et.gov.ethgov.search.domain.index.IndexId;
import et.gov.ethgov.search.domain.index.IndexRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateIndexUseCase {
    private final IndexRepository repository;
    private final Clock clock;

    public ActivateIndexUseCase(IndexRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public IndexId execute(ActivateIndexCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        IndexId id = IndexId.parse(command.indexId());
        Index aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("index not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateIndexCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String indexId,
            String reason) {}
}

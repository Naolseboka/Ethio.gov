package et.gov.ethgov.search.application.index;

import et.gov.ethgov.search.domain.index.Index;
import et.gov.ethgov.search.domain.index.IndexId;
import et.gov.ethgov.search.domain.index.IndexRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendIndexUseCase {
    private final IndexRepository repository;
    private final Clock clock;

    public SuspendIndexUseCase(IndexRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public IndexId execute(SuspendIndexCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        IndexId id = IndexId.parse(command.indexId());
        Index aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("index not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendIndexCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String indexId,
            String reason) {}
}

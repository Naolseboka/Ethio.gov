package et.gov.ethgov.search.application.index;

import et.gov.ethgov.search.domain.index.Index;
import et.gov.ethgov.search.domain.index.IndexId;
import et.gov.ethgov.search.domain.index.IndexRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateIndexUseCase {
    private final IndexRepository repository;
    private final Clock clock;

    public UpdateIndexUseCase(IndexRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public IndexId execute(UpdateIndexCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        IndexId id = IndexId.parse(command.indexId());
        Index aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("index not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateIndexCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String indexId,
            String reason) {}
}

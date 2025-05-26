package et.gov.ethgov.search.application.collection;

import et.gov.ethgov.search.domain.collection.Collection;
import et.gov.ethgov.search.domain.collection.CollectionId;
import et.gov.ethgov.search.domain.collection.CollectionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateCollectionUseCase {
    private final CollectionRepository repository;
    private final Clock clock;

    public UpdateCollectionUseCase(CollectionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CollectionId execute(UpdateCollectionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CollectionId id = CollectionId.parse(command.collectionId());
        Collection aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("collection not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateCollectionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String collectionId,
            String reason) {}
}

package et.gov.ethgov.search.application.collection;

import et.gov.ethgov.search.domain.collection.Collection;
import et.gov.ethgov.search.domain.collection.CollectionId;
import et.gov.ethgov.search.domain.collection.CollectionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCollectionUseCase {
    private final CollectionRepository repository;
    private final Clock clock;

    public CreateCollectionUseCase(CollectionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CollectionId execute(CreateCollectionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Collection aggregate = Collection.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCollectionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String collectionId,
            String reason) {}
}

package et.gov.ethgov.search.application.collection;

import et.gov.ethgov.search.domain.collection.Collection;
import et.gov.ethgov.search.domain.collection.CollectionId;
import et.gov.ethgov.search.domain.collection.CollectionRepository;
import java.util.Objects;

public final class GetCollectionUseCase {
    private final CollectionRepository repository;

    public GetCollectionUseCase(CollectionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Collection execute(String rawId) {
        CollectionId id = CollectionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("collection not found: " + rawId));
    }
}

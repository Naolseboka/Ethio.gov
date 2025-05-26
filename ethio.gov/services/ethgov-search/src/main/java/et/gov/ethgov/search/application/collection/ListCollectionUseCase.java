package et.gov.ethgov.search.application.collection;

import et.gov.ethgov.search.domain.collection.Collection;
import et.gov.ethgov.search.domain.collection.CollectionRepository;
import java.util.List;
import java.util.Objects;

public final class ListCollectionUseCase {
    private final CollectionRepository repository;

    public ListCollectionUseCase(CollectionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Collection> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}

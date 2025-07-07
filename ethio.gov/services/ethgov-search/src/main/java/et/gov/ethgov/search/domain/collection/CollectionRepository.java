package et.gov.ethgov.search.domain.collection;

import java.util.List;
import java.util.Optional;

public interface CollectionRepository {
    Collection save(Collection aggregate);

    Optional<Collection> findById(CollectionId id);

    Optional<Collection> findByReferenceCode(String tenantId, String referenceCode);

    List<Collection> findByTenant(String tenantId, int limit);

    void delete(CollectionId id);
}

package et.gov.ethgov.search.domain.index;

import java.util.List;
import java.util.Optional;

public interface IndexRepository {
    Index save(Index aggregate);

    Optional<Index> findById(IndexId id);

    Optional<Index> findByReferenceCode(String tenantId, String referenceCode);

    List<Index> findByTenant(String tenantId, int limit);

    void delete(IndexId id);
}

package et.gov.ethgov.search.domain.query;

import java.util.List;
import java.util.Optional;

public interface QueryRepository {
    Query save(Query aggregate);

    Optional<Query> findById(QueryId id);

    Optional<Query> findByReferenceCode(String tenantId, String referenceCode);

    List<Query> findByTenant(String tenantId, int limit);

    void delete(QueryId id);
}

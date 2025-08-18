package et.gov.ethgov.search.domain.searchdocument;

import java.util.List;
import java.util.Optional;

public interface SearchdocumentRepository {
    Searchdocument save(Searchdocument aggregate);

    Optional<Searchdocument> findById(SearchdocumentId id);

    Optional<Searchdocument> findByReferenceCode(String tenantId, String referenceCode);

    List<Searchdocument> findByTenant(String tenantId, int limit);

    void delete(SearchdocumentId id);
}

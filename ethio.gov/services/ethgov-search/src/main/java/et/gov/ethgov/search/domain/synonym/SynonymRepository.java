package et.gov.ethgov.search.domain.synonym;

import java.util.List;
import java.util.Optional;

public interface SynonymRepository {
    Synonym save(Synonym aggregate);

    Optional<Synonym> findById(SynonymId id);

    Optional<Synonym> findByReferenceCode(String tenantId, String referenceCode);

    List<Synonym> findByTenant(String tenantId, int limit);

    void delete(SynonymId id);
}

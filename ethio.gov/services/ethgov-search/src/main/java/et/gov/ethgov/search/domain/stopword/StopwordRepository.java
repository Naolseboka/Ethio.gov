package et.gov.ethgov.search.domain.stopword;

import java.util.List;
import java.util.Optional;

public interface StopwordRepository {
    Stopword save(Stopword aggregate);

    Optional<Stopword> findById(StopwordId id);

    Optional<Stopword> findByReferenceCode(String tenantId, String referenceCode);

    List<Stopword> findByTenant(String tenantId, int limit);

    void delete(StopwordId id);
}

package et.gov.ethgov.developer.domain.docsversion;

import java.util.List;
import java.util.Optional;

public interface DocsversionRepository {
    Docsversion save(Docsversion aggregate);

    Optional<Docsversion> findById(DocsversionId id);

    Optional<Docsversion> findByReferenceCode(String tenantId, String referenceCode);

    List<Docsversion> findByTenant(String tenantId, int limit);

    void delete(DocsversionId id);
}

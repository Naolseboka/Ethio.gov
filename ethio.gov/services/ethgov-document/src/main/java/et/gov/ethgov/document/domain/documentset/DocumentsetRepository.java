package et.gov.ethgov.document.domain.documentset;

import java.util.List;
import java.util.Optional;

public interface DocumentsetRepository {
    Documentset save(Documentset aggregate);

    Optional<Documentset> findById(DocumentsetId id);

    Optional<Documentset> findByReferenceCode(String tenantId, String referenceCode);

    List<Documentset> findByTenant(String tenantId, int limit);

    void delete(DocumentsetId id);
}

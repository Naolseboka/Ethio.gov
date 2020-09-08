package et.gov.ethgov.document.domain.accessgrant;

import java.util.List;
import java.util.Optional;

public interface AccessgrantRepository {
    Accessgrant save(Accessgrant aggregate);

    Optional<Accessgrant> findById(AccessgrantId id);

    Optional<Accessgrant> findByReferenceCode(String tenantId, String referenceCode);

    List<Accessgrant> findByTenant(String tenantId, int limit);

    void delete(AccessgrantId id);
}

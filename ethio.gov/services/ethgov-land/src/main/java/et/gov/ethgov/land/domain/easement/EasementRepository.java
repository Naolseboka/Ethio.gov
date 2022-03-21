package et.gov.ethgov.land.domain.easement;

import java.util.List;
import java.util.Optional;

public interface EasementRepository {
    Easement save(Easement aggregate);

    Optional<Easement> findById(EasementId id);

    Optional<Easement> findByReferenceCode(String tenantId, String referenceCode);

    List<Easement> findByTenant(String tenantId, int limit);

    void delete(EasementId id);
}

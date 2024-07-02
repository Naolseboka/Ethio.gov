package et.gov.ethgov.permits.domain.permitchecklist;

import java.util.List;
import java.util.Optional;

public interface PermitchecklistRepository {
    Permitchecklist save(Permitchecklist aggregate);

    Optional<Permitchecklist> findById(PermitchecklistId id);

    Optional<Permitchecklist> findByReferenceCode(String tenantId, String referenceCode);

    List<Permitchecklist> findByTenant(String tenantId, int limit);

    void delete(PermitchecklistId id);
}

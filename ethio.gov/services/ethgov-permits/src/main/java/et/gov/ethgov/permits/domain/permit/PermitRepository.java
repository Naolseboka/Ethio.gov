package et.gov.ethgov.permits.domain.permit;

import java.util.List;
import java.util.Optional;

public interface PermitRepository {
    Permit save(Permit aggregate);

    Optional<Permit> findById(PermitId id);

    Optional<Permit> findByReferenceCode(String tenantId, String referenceCode);

    List<Permit> findByTenant(String tenantId, int limit);

    void delete(PermitId id);
}

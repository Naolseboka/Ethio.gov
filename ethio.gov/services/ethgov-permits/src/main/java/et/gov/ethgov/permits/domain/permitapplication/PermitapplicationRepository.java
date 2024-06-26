package et.gov.ethgov.permits.domain.permitapplication;

import java.util.List;
import java.util.Optional;

public interface PermitapplicationRepository {
    Permitapplication save(Permitapplication aggregate);

    Optional<Permitapplication> findById(PermitapplicationId id);

    Optional<Permitapplication> findByReferenceCode(String tenantId, String referenceCode);

    List<Permitapplication> findByTenant(String tenantId, int limit);

    void delete(PermitapplicationId id);
}

package et.gov.ethgov.permits.domain.permitcondition;

import java.util.List;
import java.util.Optional;

public interface PermitconditionRepository {
    Permitcondition save(Permitcondition aggregate);

    Optional<Permitcondition> findById(PermitconditionId id);

    Optional<Permitcondition> findByReferenceCode(String tenantId, String referenceCode);

    List<Permitcondition> findByTenant(String tenantId, int limit);

    void delete(PermitconditionId id);
}

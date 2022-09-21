package et.gov.ethgov.licensing.domain.suspension;

import java.util.List;
import java.util.Optional;

public interface SuspensionRepository {
    Suspension save(Suspension aggregate);

    Optional<Suspension> findById(SuspensionId id);

    Optional<Suspension> findByReferenceCode(String tenantId, String referenceCode);

    List<Suspension> findByTenant(String tenantId, int limit);

    void delete(SuspensionId id);
}

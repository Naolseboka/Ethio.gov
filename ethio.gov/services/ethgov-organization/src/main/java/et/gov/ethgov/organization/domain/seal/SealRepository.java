package et.gov.ethgov.organization.domain.seal;

import java.util.List;
import java.util.Optional;

public interface SealRepository {
    Seal save(Seal aggregate);

    Optional<Seal> findById(SealId id);

    Optional<Seal> findByReferenceCode(String tenantId, String referenceCode);

    List<Seal> findByTenant(String tenantId, int limit);

    void delete(SealId id);
}

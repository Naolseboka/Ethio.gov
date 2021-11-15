package et.gov.ethgov.identity.domain.delegation;

import java.util.List;
import java.util.Optional;

public interface DelegationRepository {
    Delegation save(Delegation aggregate);

    Optional<Delegation> findById(DelegationId id);

    Optional<Delegation> findByReferenceCode(String tenantId, String referenceCode);

    List<Delegation> findByTenant(String tenantId, int limit);

    void delete(DelegationId id);
}

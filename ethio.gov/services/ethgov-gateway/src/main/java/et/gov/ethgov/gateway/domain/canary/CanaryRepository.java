package et.gov.ethgov.gateway.domain.canary;

import java.util.List;
import java.util.Optional;

public interface CanaryRepository {
    Canary save(Canary aggregate);

    Optional<Canary> findById(CanaryId id);

    Optional<Canary> findByReferenceCode(String tenantId, String referenceCode);

    List<Canary> findByTenant(String tenantId, int limit);

    void delete(CanaryId id);
}

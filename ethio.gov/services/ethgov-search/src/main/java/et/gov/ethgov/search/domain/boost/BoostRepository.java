package et.gov.ethgov.search.domain.boost;

import java.util.List;
import java.util.Optional;

public interface BoostRepository {
    Boost save(Boost aggregate);

    Optional<Boost> findById(BoostId id);

    Optional<Boost> findByReferenceCode(String tenantId, String referenceCode);

    List<Boost> findByTenant(String tenantId, int limit);

    void delete(BoostId id);
}

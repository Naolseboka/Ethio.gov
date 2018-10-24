package et.gov.ethgov.analytics.domain.attribution;

import java.util.List;
import java.util.Optional;

public interface AttributionRepository {
    Attribution save(Attribution aggregate);

    Optional<Attribution> findById(AttributionId id);

    Optional<Attribution> findByReferenceCode(String tenantId, String referenceCode);

    List<Attribution> findByTenant(String tenantId, int limit);

    void delete(AttributionId id);
}

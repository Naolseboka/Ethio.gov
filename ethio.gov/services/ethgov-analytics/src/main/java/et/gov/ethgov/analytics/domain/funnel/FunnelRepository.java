package et.gov.ethgov.analytics.domain.funnel;

import java.util.List;
import java.util.Optional;

public interface FunnelRepository {
    Funnel save(Funnel aggregate);

    Optional<Funnel> findById(FunnelId id);

    Optional<Funnel> findByReferenceCode(String tenantId, String referenceCode);

    List<Funnel> findByTenant(String tenantId, int limit);

    void delete(FunnelId id);
}

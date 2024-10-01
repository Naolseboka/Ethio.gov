package et.gov.ethgov.reporting.domain.dashboard;

import java.util.List;
import java.util.Optional;

public interface DashboardRepository {
    Dashboard save(Dashboard aggregate);

    Optional<Dashboard> findById(DashboardId id);

    Optional<Dashboard> findByReferenceCode(String tenantId, String referenceCode);

    List<Dashboard> findByTenant(String tenantId, int limit);

    void delete(DashboardId id);
}

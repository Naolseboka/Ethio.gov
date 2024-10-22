package et.gov.ethgov.reporting.domain.kpi;

import java.util.List;
import java.util.Optional;

public interface KpiRepository {
    Kpi save(Kpi aggregate);

    Optional<Kpi> findById(KpiId id);

    Optional<Kpi> findByReferenceCode(String tenantId, String referenceCode);

    List<Kpi> findByTenant(String tenantId, int limit);

    void delete(KpiId id);
}

package et.gov.ethgov.revenue.domain.fiscalyear;

import java.util.List;
import java.util.Optional;

public interface FiscalyearRepository {
    Fiscalyear save(Fiscalyear aggregate);

    Optional<Fiscalyear> findById(FiscalyearId id);

    Optional<Fiscalyear> findByReferenceCode(String tenantId, String referenceCode);

    List<Fiscalyear> findByTenant(String tenantId, int limit);

    void delete(FiscalyearId id);
}

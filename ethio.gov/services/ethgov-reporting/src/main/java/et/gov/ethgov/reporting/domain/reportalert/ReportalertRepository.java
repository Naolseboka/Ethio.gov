package et.gov.ethgov.reporting.domain.reportalert;

import java.util.List;
import java.util.Optional;

public interface ReportalertRepository {
    Reportalert save(Reportalert aggregate);

    Optional<Reportalert> findById(ReportalertId id);

    Optional<Reportalert> findByReferenceCode(String tenantId, String referenceCode);

    List<Reportalert> findByTenant(String tenantId, int limit);

    void delete(ReportalertId id);
}

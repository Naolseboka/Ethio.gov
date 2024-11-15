package et.gov.ethgov.reporting.domain.reportfilter;

import java.util.List;
import java.util.Optional;

public interface ReportfilterRepository {
    Reportfilter save(Reportfilter aggregate);

    Optional<Reportfilter> findById(ReportfilterId id);

    Optional<Reportfilter> findByReferenceCode(String tenantId, String referenceCode);

    List<Reportfilter> findByTenant(String tenantId, int limit);

    void delete(ReportfilterId id);
}

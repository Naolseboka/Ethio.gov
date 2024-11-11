package et.gov.ethgov.reporting.domain.reportexport;

import java.util.List;
import java.util.Optional;

public interface ReportexportRepository {
    Reportexport save(Reportexport aggregate);

    Optional<Reportexport> findById(ReportexportId id);

    Optional<Reportexport> findByReferenceCode(String tenantId, String referenceCode);

    List<Reportexport> findByTenant(String tenantId, int limit);

    void delete(ReportexportId id);
}

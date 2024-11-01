package et.gov.ethgov.reporting.domain.report;

import java.util.List;
import java.util.Optional;

public interface ReportRepository {
    Report save(Report aggregate);

    Optional<Report> findById(ReportId id);

    Optional<Report> findByReferenceCode(String tenantId, String referenceCode);

    List<Report> findByTenant(String tenantId, int limit);

    void delete(ReportId id);
}

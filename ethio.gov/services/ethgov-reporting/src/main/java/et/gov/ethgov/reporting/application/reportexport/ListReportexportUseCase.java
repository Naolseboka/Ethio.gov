package et.gov.ethgov.reporting.application.reportexport;

import et.gov.ethgov.reporting.domain.reportexport.Reportexport;
import et.gov.ethgov.reporting.domain.reportexport.ReportexportRepository;
import java.util.List;
import java.util.Objects;

public final class ListReportexportUseCase {
    private final ReportexportRepository repository;

    public ListReportexportUseCase(ReportexportRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Reportexport> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}

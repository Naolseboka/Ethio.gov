package et.gov.ethgov.reporting.application.report;

import et.gov.ethgov.reporting.domain.report.Report;
import et.gov.ethgov.reporting.domain.report.ReportRepository;
import java.util.List;
import java.util.Objects;

public final class ListReportUseCase {
    private final ReportRepository repository;

    public ListReportUseCase(ReportRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Report> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}

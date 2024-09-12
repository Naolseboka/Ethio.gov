package et.gov.ethgov.reporting.application.reportfilter;

import et.gov.ethgov.reporting.domain.reportfilter.Reportfilter;
import et.gov.ethgov.reporting.domain.reportfilter.ReportfilterRepository;
import java.util.List;
import java.util.Objects;

public final class ListReportfilterUseCase {
    private final ReportfilterRepository repository;

    public ListReportfilterUseCase(ReportfilterRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Reportfilter> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}

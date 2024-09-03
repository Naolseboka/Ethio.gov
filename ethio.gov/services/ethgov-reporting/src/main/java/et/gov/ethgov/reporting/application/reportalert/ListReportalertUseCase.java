package et.gov.ethgov.reporting.application.reportalert;

import et.gov.ethgov.reporting.domain.reportalert.Reportalert;
import et.gov.ethgov.reporting.domain.reportalert.ReportalertRepository;
import java.util.List;
import java.util.Objects;

public final class ListReportalertUseCase {
    private final ReportalertRepository repository;

    public ListReportalertUseCase(ReportalertRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Reportalert> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}

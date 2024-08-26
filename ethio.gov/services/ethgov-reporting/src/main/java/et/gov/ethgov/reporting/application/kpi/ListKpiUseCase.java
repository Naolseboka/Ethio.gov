package et.gov.ethgov.reporting.application.kpi;

import et.gov.ethgov.reporting.domain.kpi.Kpi;
import et.gov.ethgov.reporting.domain.kpi.KpiRepository;
import java.util.List;
import java.util.Objects;

public final class ListKpiUseCase {
    private final KpiRepository repository;

    public ListKpiUseCase(KpiRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Kpi> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}

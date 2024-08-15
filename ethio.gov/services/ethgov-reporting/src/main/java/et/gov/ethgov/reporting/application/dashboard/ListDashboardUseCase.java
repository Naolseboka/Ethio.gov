package et.gov.ethgov.reporting.application.dashboard;

import et.gov.ethgov.reporting.domain.dashboard.Dashboard;
import et.gov.ethgov.reporting.domain.dashboard.DashboardRepository;
import java.util.List;
import java.util.Objects;

public final class ListDashboardUseCase {
    private final DashboardRepository repository;

    public ListDashboardUseCase(DashboardRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Dashboard> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}

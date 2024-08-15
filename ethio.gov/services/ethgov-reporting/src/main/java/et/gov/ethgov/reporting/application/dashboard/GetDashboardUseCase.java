package et.gov.ethgov.reporting.application.dashboard;

import et.gov.ethgov.reporting.domain.dashboard.Dashboard;
import et.gov.ethgov.reporting.domain.dashboard.DashboardId;
import et.gov.ethgov.reporting.domain.dashboard.DashboardRepository;
import java.util.Objects;

public final class GetDashboardUseCase {
    private final DashboardRepository repository;

    public GetDashboardUseCase(DashboardRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Dashboard execute(String rawId) {
        DashboardId id = DashboardId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("dashboard not found: " + rawId));
    }
}

package et.gov.ethgov.reporting.application.dashboard;

import et.gov.ethgov.reporting.domain.dashboard.Dashboard;
import et.gov.ethgov.reporting.domain.dashboard.DashboardId;
import et.gov.ethgov.reporting.domain.dashboard.DashboardRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateDashboardUseCase {
    private final DashboardRepository repository;
    private final Clock clock;

    public ActivateDashboardUseCase(DashboardRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DashboardId execute(ActivateDashboardCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DashboardId id = DashboardId.parse(command.dashboardId());
        Dashboard aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("dashboard not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateDashboardCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String dashboardId,
            String reason) {}
}

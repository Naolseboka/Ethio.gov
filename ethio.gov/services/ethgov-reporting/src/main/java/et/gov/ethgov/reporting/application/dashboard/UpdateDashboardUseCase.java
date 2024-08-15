package et.gov.ethgov.reporting.application.dashboard;

import et.gov.ethgov.reporting.domain.dashboard.Dashboard;
import et.gov.ethgov.reporting.domain.dashboard.DashboardId;
import et.gov.ethgov.reporting.domain.dashboard.DashboardRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateDashboardUseCase {
    private final DashboardRepository repository;
    private final Clock clock;

    public UpdateDashboardUseCase(DashboardRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DashboardId execute(UpdateDashboardCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DashboardId id = DashboardId.parse(command.dashboardId());
        Dashboard aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("dashboard not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateDashboardCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String dashboardId,
            String reason) {}
}
